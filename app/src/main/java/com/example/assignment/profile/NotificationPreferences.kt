package com.example.assignment.profile

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Needs this dependency in app-level build.gradle:
// implementation("androidx.datastore:datastore-preferences:1.1.1")

val Context.notificationDataStore by preferencesDataStore(name = "notification_settings")

private object NotificationPrefsKeys {
    val APPT_REMINDERS = booleanPreferencesKey("appt_reminders")
    val MED_REMINDERS = booleanPreferencesKey("med_reminders")
    val RECORD_UPDATES = booleanPreferencesKey("record_updates")
    val EMERGENCY_ALERTS = booleanPreferencesKey("emergency_alerts")
    val HEALTH_TIPS = booleanPreferencesKey("health_tips")
}

data class NotificationPrefs(
    val apptReminders: Boolean = true,
    val medReminders: Boolean = true,
    val recordUpdates: Boolean = true,
    val emergencyAlerts: Boolean = true,
    val healthTips: Boolean = false
)

// Used by NotificationSettingsScreen on open — a Flow so it stays in sync if changed elsewhere
fun notificationPrefsFlow(context: Context): Flow<NotificationPrefs> =
    context.notificationDataStore.data.map { prefs ->
        NotificationPrefs(
            apptReminders = prefs[NotificationPrefsKeys.APPT_REMINDERS] ?: true,
            medReminders = prefs[NotificationPrefsKeys.MED_REMINDERS] ?: true,
            recordUpdates = prefs[NotificationPrefsKeys.RECORD_UPDATES] ?: true,
            emergencyAlerts = prefs[NotificationPrefsKeys.EMERGENCY_ALERTS] ?: true,
            healthTips = prefs[NotificationPrefsKeys.HEALTH_TIPS] ?: false
        )
    }

// Used by the "Save Settings" button
suspend fun saveNotificationPrefs(context: Context, prefs: NotificationPrefs) {
    context.notificationDataStore.edit { settings ->
        settings[NotificationPrefsKeys.APPT_REMINDERS] = prefs.apptReminders
        settings[NotificationPrefsKeys.MED_REMINDERS] = prefs.medReminders
        settings[NotificationPrefsKeys.RECORD_UPDATES] = prefs.recordUpdates
        settings[NotificationPrefsKeys.EMERGENCY_ALERTS] = prefs.emergencyAlerts
        settings[NotificationPrefsKeys.HEALTH_TIPS] = prefs.healthTips
    }
}