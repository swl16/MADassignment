package com.example.assignment.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Record
import com.example.assignment.database.RecordCategory
import com.example.assignment.database.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class RecordViewModel(private val repository: RecordRepository) : ViewModel() {

    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> = _records.asStateFlow()

    private var currentUsername: String = ""

    private val _selectedRecord = MutableStateFlow<Record?>(null)
    val selectedRecord: StateFlow<Record?> = _selectedRecord.asStateFlow()

    fun setUsername(username: String) {
        currentUsername = username
    }

    fun fetchRecords() {
        if (currentUsername.isBlank()) return
        viewModelScope.launch {
            repository.observeRecords(currentUsername).collect {
                _records.value = it
            }
        }
    }

    fun fetchRecordsByCategory(category: RecordCategory) {
        if (currentUsername.isBlank()) return
        viewModelScope.launch {
            repository.observeRecordsByCategory(currentUsername, category).collect {
                _records.value = it
            }
        }
    }

    fun loadRecord(id: String) {
        viewModelScope.launch {
            _selectedRecord.value = repository.getLocalRecordById(id)
        }
    }

    suspend fun getRecordById(id: String): Record? = repository.getLocalRecordById(id)
    suspend fun getSignedUrl(filePath: String): String? = repository.getSignedUrl(filePath)

    fun getFileName(uri: Uri): String? = repository.queryFileName(uri)
    fun getFileSize(uri: Uri): Long? = repository.queryFileSize(uri)

    fun uploadRecord(
        fileUri: Uri,
        title: String,
        category: RecordCategory,
        recordDateMillis: Long,
        provider: String,
        onComplete: () -> Unit = {}
    ) {
        if (currentUsername.isBlank()) return
        viewModelScope.launch {
            repository.uploadRecord(currentUsername, fileUri, title, category, recordDateMillis, provider)
            onComplete()
        }
    }

    fun updateRecord(
        record: Record,
        newTitle: String,
        newCategory: RecordCategory,
        newDateMillis: Long,
        newProvider: String?
    ) {
        viewModelScope.launch {
            val updated = record.copy(
                title = newTitle,
                categoryName = newCategory.name,
                recordDateMillis = newDateMillis,
                recordDate = Instant.ofEpochMilli(newDateMillis).toString(),
                provider = newProvider
            )
            repository.updateRecord(updated)
            _selectedRecord.value = updated
        }
    }

    fun updateRecord(record: Record, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateRecord(record)
            onComplete()
        }
    }

    fun deleteRecord(record: Record, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteRecord(record)
            onComplete()
        }
    }

    fun syncRecords() {
        if (currentUsername.isBlank()) return
        viewModelScope.launch {
            repository.syncFromRemote(currentUsername)
        }
    }
}
