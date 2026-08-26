package com.example.assignment.database

data class Reminder (
    var documentId: String = "",

    val username :String = "",

    val medicineName : String = "",
    val dosage : String = "",
    val frequency: String = "",
    val time: String = "",
    val instructions: String = "",
    val isNotificationEnabled: Boolean = true,
    val isActive: Boolean = true
)