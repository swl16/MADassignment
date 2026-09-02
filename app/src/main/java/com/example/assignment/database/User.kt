package com.example.assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = false)
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val mobileNumber: String,
    val dateOfBirth: String
)