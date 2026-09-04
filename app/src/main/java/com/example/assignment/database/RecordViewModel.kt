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
        fileUri: android.net.Uri,
        title: String,
        category: RecordCategory,
        recordDateMillis: Long,
        provider: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                android.util.Log.d("SupabaseDebug", "1. Starting uploadRecord in ViewModel...")

                // Check username state
                val currentUsername = currentUsername // or whatever variable holds your current username
                android.util.Log.d("SupabaseDebug", "Current username is: '$currentUsername'")

                repository.uploadRecord(
                    username = currentUsername,
                    fileUri = fileUri,
                    title = title,
                    category = category,
                    recordDateMillis = recordDateMillis,
                    provider = provider
                )

                android.util.Log.d("SupabaseDebug", "2. Upload completed successfully!")
            } catch (e: Exception) {
                android.util.Log.e("SupabaseError", "Error during upload: ${e.message}", e)
            } finally {
                // Guarantees screen navigation happens ONLY after the coroutine finishes
                onComplete()
            }
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
