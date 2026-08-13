package com.example.assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Room will automatically count 1, 2, 3 for every new user
    val fullName: String,
    val email: String,
    val password: String,
    val mobileNumber: String,
    val dateOfBirth: String
)