package com.example.assignment.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Record
import com.example.assignment.database.RecordCategory
import com.example.assignment.database.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecordViewModel(private val repository: RecordRepository) : ViewModel() {

    fun getRecords(username: String): Flow<List<Record>> = repository.observeRecords(username)

    fun getRecordsByCategory(username: String, category: RecordCategory): Flow<List<Record>> =
        repository.getRecordsByCategory(username, category)

    fun getRecentRecords(username: String, limit: Int): Flow<List<Record>> =
        repository.getRecentRecords(username, limit)

    fun searchRecords(username: String, query: String): Flow<List<Record>> =
        repository.searchRecords(username, query)

    fun searchRecordsInCategory(username: String, category: RecordCategory, query: String): Flow<List<Record>> =
        repository.searchRecordsInCategory(username, category, query)

    fun getCategoryCount(username: String, category: RecordCategory): Flow<Int> =
        repository.getCategoryCount(username, category)

    suspend fun getRecordById(id: String): Record? = repository.getLocalRecordById(id)

    fun uploadRecord(
        username: String,
        fileUri: Uri,
        title: String,
        category: RecordCategory,
        recordDateMillis: Long,
        provider: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.uploadRecord(username, fileUri, title, category, recordDateMillis, provider)
            onComplete()
        }
    }

    fun updateRecord(record: Record, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateRecord(record)
            onComplete()
        }
    }

    fun deleteRecord(record: Record, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteRecord(record)
            onComplete()
        }
    }

    fun syncRecords(username: String) {
        viewModelScope.launch {
            repository.syncFromRemote(username)
        }
    }
}
