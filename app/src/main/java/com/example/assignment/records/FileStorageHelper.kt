package com.example.assignment.records

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.UUID

class FileStorageHelper(private val context: Context) {

    private val recordsDir: File
        get() = File(context.filesDir, "medical_records").apply { if (!exists()) mkdirs() }

    fun copyToInternalStorage(uri: Uri): Pair<String, String> {
        val originalName = getFileName(uri) ?: "file"
        val extension = originalName.substringAfterLast('.', "").ifBlank { "pdf" }
        val fileType = extension.uppercase()

        val savedFileName = "${UUID.randomUUID()}.$extension"
        val destinationFile = File(recordsDir, savedFileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return savedFileName to fileType
    }

    fun getFile(fileName: String): File = File(recordsDir, fileName)

    fun deleteFile(fileName: String): Boolean = getFile(fileName).delete()

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}