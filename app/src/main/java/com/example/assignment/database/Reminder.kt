package com.example.assignment.database
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "reminders")
@Serializable
data class Reminder(
    @PrimaryKey
    @SerialName("id")
    val id: String = java.util.UUID.randomUUID().toString(),

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