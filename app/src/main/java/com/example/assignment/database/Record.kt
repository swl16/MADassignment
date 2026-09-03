package com.example.assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "records")
@Serializable
data class Record(
    @PrimaryKey
    @SerialName("id")
    val id: String = "",

    @SerialName("username")
    val username: String = "",

    @SerialName("title")
    val title: String = "",

    @SerialName("category")
    val categoryName: String = "",

    @SerialName("file_path")
    val filePath: String = "",

    @SerialName("file_type")
    val fileType: String = "",

    @SerialName("provider")
    val provider: String? = null,

    @SerialName("record_date")
    val recordDate: String = "",

    @SerialName("uploaded_at")
    val uploadedAt: String = "",

    @SerialName("record_date_millis")
    val recordDateMillis: Long = 0L,

    @SerialName("uploaded_at_millis")
    val uploadedAtMillis: Long = 0L,

    @SerialName("file_name")
    val fileName: String = ""
) {
    val category: RecordCategory
        get() = try {
            RecordCategory.entries.find { it.name == categoryName } ?: RecordCategory.LAB_RESULTS
        } catch (e: Exception) {
            RecordCategory.LAB_RESULTS
        }
}