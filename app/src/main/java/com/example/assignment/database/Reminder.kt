package com.example.assignment.database
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    @SerialName("id")
    val id: String? = null, // Supabase generates this UUID automatically

    @SerialName("username")
    val username: String = "",

    @SerialName("medicine_name")
    val medicineName: String = "",

    val dosage: String = "",
    val frequency: String = "",
    val time: String = "",
    val instructions: String = "",

    @SerialName("is_notification_enabled")
    val isNotificationEnabled: Boolean = true,

    @SerialName("is_active")
    val isActive: Boolean = true
)