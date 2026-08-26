package com.example.assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,     // links this appointment to a User's username
    val doctorName: String,
    val specialty: String,
    val date: String,         // stored as "Thursday, 16 July 2026" (display-ready)
    val time: String,         // "10:30 AM"
    val location: String = "HealthCare Clinic",
    val status: String = "Upcoming", // Upcoming / Completed / Canceled
    val reason: String = "General health consultation"
)