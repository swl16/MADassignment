package com.example.assignment.database

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

private const val BUCKET_RECORDS = "medical-records"

class RecordRepository(
    private val dao: RecordDao,
    private val context: Context
) {
    private val supabase = SupabaseService.client
    private val table = supabase.from("records")

    fun observeRecords(username: String): Flow<List<Record>> {
        return dao.getLocalRecords(username)
    }

    fun observeRecordsByCategory(username: String, category: RecordCategory): Flow<List<Record>> {
        return dao.getLocalRecordsByCategory(username, category.name)
    }

    fun getRecordsByCategory(username: String, category: RecordCategory): Flow<List<Record>> {
        return dao.getLocalRecordsByCategory(username, category.name)
    }

    fun searchRecordsInCategory(username: String, category: RecordCategory, query: String): Flow<List<Record>> {
        return dao.searchRecordsInCategory(username, category.name, query)
    }

    fun getRecentRecords(username: String, limit: Int): Flow<List<Record>> {
        return dao.getRecentRecords(username, limit)
    }

    fun searchRecords(username: String, query: String): Flow<List<Record>> {
        return dao.searchRecords(username, query)
    }

    fun getCategoryCount(username: String, category: RecordCategory): Flow<Int> {
        return dao.getCategoryCount(username, category.name)
    }

    suspend fun getLocalRecordById(id: String): Record? {
        return dao.getLocalRecordById(id)
    }

    suspend fun syncFromRemote(username: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteData = table.select { filter { eq("username", username) } }.decodeList<Record>()
                dao.insertRecords(remoteData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun uploadRecord(
        username: String,
        fileUri: Uri,
        title: String,
        category: RecordCategory,
        recordDateMillis: Long,
        provider: String
    ) {
        withContext(Dispatchers.IO) {
            val (filePath, fileType) = uploadFileToStorage(username, fileUri)
            val record = Record(
                id = UUID.randomUUID().toString(),
                username = username,
                title = title,
                categoryName = category.name,
                filePath = filePath,
                fileType = fileType,
                provider = provider.ifBlank { null },
                recordDate = Instant.ofEpochMilli(recordDateMillis).toString(),
                uploadedAt = Instant.now().toString(),
                recordDateMillis = recordDateMillis,
                uploadedAtMillis = System.currentTimeMillis(),
                fileName = queryFileName(fileUri) ?: "file"
            )
            saveRecord(record)
        }
    }

    suspend fun saveRecord(record: Record) {
        withContext(Dispatchers.IO) {
            dao.insertRecord(record)
            try {
                table.upsert(record)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun updateRecord(record: Record) {
        withContext(Dispatchers.IO) {
            dao.updateRecord(record)
            try {
                table.update(record) { filter { eq("id", record.id) } }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteRecord(record: Record) {
        withContext(Dispatchers.IO) {
            dao.delete(record)
            try {
                if (record.filePath.isNotEmpty()) {
                    supabase.storage.from(BUCKET_RECORDS).delete(listOf(record.filePath))
                }
                table.delete { filter { eq("id", record.id) } }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getSignedUrl(filePath: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                supabase.storage.from(BUCKET_RECORDS).createSignedUrl(filePath, 5.minutes)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private suspend fun uploadFileToStorage(username: String, uri: Uri): Pair<String, String> {
        val originalName = queryFileName(uri) ?: "file"
        val extension = originalName.substringAfterLast('.', "").ifBlank { "pdf" }
        val fileType = extension.uppercase()
        val path = "$username/${UUID.randomUUID()}.$extension"

        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Could not read the selected file")

        supabase.storage.from(BUCKET_RECORDS).upload(path, bytes)
        return path to fileType
    }

    fun queryFileName(uri: Uri): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) name = cursor.getString(nameIndex)
        }
        return name
    }

    fun queryFileSize(uri: Uri): Long? {
        var size: Long? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
        }
        return size
    }
}
