package com.example.assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,  // links this contact to a User's username
    val fullName: String,
    val relationship: String,
    val mobileNumber: String
)