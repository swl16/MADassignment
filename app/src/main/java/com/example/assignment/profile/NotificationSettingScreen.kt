package com.example.assignment.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun NotificationSettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State variables for the toggle switches
    var apptReminders by remember { mutableStateOf(true) }
    var medReminders by remember { mutableStateOf(true) }
    var recordUpdates by remember { mutableStateOf(true) }
    var emergencyAlerts by remember { mutableStateOf(true) }
    var healthTips by remember { mutableStateOf(false) }

    // 1. Load whatever was saved last time, as soon as the screen opens
    LaunchedEffect(Unit) {
        notificationPrefsFlow(context).collect { prefs ->
            apptReminders = prefs.apptReminders
            medReminders = prefs.medReminders
            recordUpdates = prefs.recordUpdates
            emergencyAlerts = prefs.emergencyAlerts
            healthTips = prefs.healthTips
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        StandardTopBar(
            title = "Notification Settings", actionText = "",
            onBackClick = onNavigateBack, onActionClick = {}
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Choose which alerts you want to receive.", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // Toggle Cards
            ToggleSettingCard("Appointment reminders", "Confirmation, changes and upcoming visits", apptReminders) { apptReminders = it }
            ToggleSettingCard("Medication reminders", "Alerts when medicine is due", medReminders) { medReminders = it }
            ToggleSettingCard("Medical record updates", "New or updated health documents", recordUpdates) { recordUpdates = it }
            ToggleSettingCard("Emergency alerts", "Important emergency contact activity", emergencyAlerts) { emergencyAlerts = it }
            ToggleSettingCard("Health tips", "Weekly health and wellness suggestions", healthTips) { healthTips = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Do Not Disturb Card (Special Light Blue Card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5EDFF)), // Light Blue
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Do Not Disturb", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E50FF))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Pause all notifications between 10 PM–7 AM", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Settings Button — actually persists now
            Button(
                onClick = {
                    scope.launch {
                        saveNotificationPrefs(
                            context,
                            NotificationPrefs(
                                apptReminders = apptReminders,
                                medReminders = medReminders,
                                recordUpdates = recordUpdates,
                                emergencyAlerts = emergencyAlerts,
                                healthTips = healthTips
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
            ) {
                Text(text = "Save Settings", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ToggleSettingCard(title: String, subtitle: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF1E50FF),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFD1D1D6),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}