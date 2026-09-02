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

    val title: String = "",
    
    @SerialName("category")
    val categoryName: String = "",

    @SerialName("file_path")
    val filePath: String = "",

    @SerialName("file_type")
    val fileType: String = "",

    val provider: String? = null,

    @SerialName("record_date")
    val recordDate: String = "",
    @SerialName("uploaded_at")
    val uploadedAt: String = "",

    val recordDateMillis: Long = 0L,
    val uploadedAtMillis: Long = 0L,
    val fileName: String = ""
) {
    val category: RecordCategory
        get() = try {
            RecordCategory.entries.find { it.name == categoryName } ?: RecordCategory.LAB_RESULTS
        } catch (e: Exception) {
            RecordCategory.LAB_RESULTS
        }
}
