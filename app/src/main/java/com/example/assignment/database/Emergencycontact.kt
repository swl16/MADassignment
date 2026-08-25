package com.example.assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,       // one contact per user for now (matches the current single-contact UI)
    val fullName: String,
    val relationship: String,
    val mobileNumber: String
)