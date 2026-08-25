package com.example.assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val category: RecordCategory,
    val fileName: String,
    val fileType: String,
    val recordDateMillis: Long,
    val uploadedAtMillis: Long,
    val provider: String? = null
)