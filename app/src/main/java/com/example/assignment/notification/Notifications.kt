package com.example.assignment.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.AppointmentDao
import com.example.assignment.database.ReminderViewModel
import com.example.assignment.profile.StandardTopBar

@Composable
fun NotificationsScreen(
    username: String,
    appointmentDao: AppointmentDao,
    reminderViewModel: ReminderViewModel,
    onNavigateBack: () -> Unit
) {
    val appointments by appointmentDao.getAppointmentsForUser(username).collectAsState(initial = emptyList())
    val reminders by reminderViewModel.reminders.collectAsState()

    // Tracks which notification keys have been marked read (this session)
    var readIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(username) {
        reminderViewModel.setUsername(username)
        reminderViewModel.fetchReminders()
        reminderViewModel.syncReminders()
    }

    val upcomingAppointments = appointments.filter { it.status == "Upcoming" }
    val activeReminders = reminders.filter { it.isActive && it.isNotificationEnabled }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        StandardTopBar(
            title = "Notifications",
            actionText = "Mark all read",
            onBackClick = onNavigateBack,
            onActionClick = {
                // Mark every currently visible notification as read
                val appointmentKeys = upcomingAppointments.map { "appt_${it.id}" }
                val reminderKeys = activeReminders.map { "rem_${it.id}" }
                readIds = readIds + appointmentKeys + reminderKeys
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (upcomingAppointments.isEmpty() && activeReminders.isEmpty()) {
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = "No notifications yet",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                if (upcomingAppointments.isNotEmpty()) {
                    Text(text = "Appointments", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78849E))
                    Spacer(modifier = Modifier.height(12.dp))

                    upcomingAppointments.forEach { appointment ->
                        val key = "appt_${appointment.id}"
                        NotificationCard(
                            icon = "✓", iconColor = Color.White, iconBg = Color(0xFF1E50FF),
                            title = "Appointment with ${appointment.doctorName}",
                            description = "${appointment.specialty} • ${appointment.location}",
                            time = "${appointment.date}, ${appointment.time}",
                            isUnread = key !in readIds,
                            onClick = { readIds = readIds + key } // NEW: tap to mark read
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (activeReminders.isNotEmpty()) {
                    Text(text = "Medication Reminders", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78849E))
                    Spacer(modifier = Modifier.height(12.dp))

                    activeReminders.forEach { reminder ->
                        val key = "rem_${reminder.id}"
                        NotificationCard(
                            icon = "!", iconColor = Color.White, iconBg = Color(0xFFFFA000),
                            title = "Take ${reminder.medicineName}",
                            description = if (reminder.instructions.isNullOrBlank()) {
                                "${reminder.dosage} • ${reminder.instructions}"
                            } else {
                                "${reminder.dosage} • ${reminder.frequency}"
                            },
                            time = reminder.time,
                            isUnread = key !in readIds,
                            onClick = { readIds = readIds + key } // NEW: tap to mark read
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NotificationCard(
    icon: String, iconColor: Color, iconBg: Color,
    title: String, description: String, time: String,
    isUnread: Boolean,
    onClick: () -> Unit = {} // NEW
) {
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(onClick = onClick), // NEW: tap the whole card to mark read
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconBg,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = icon, color = iconColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = description, fontSize = 12.sp, color = Color.Gray, lineHeight = 18.sp)

                    if (time.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = time, fontSize = 10.sp, color = Color(0xFFA0AABF))
                    }
                }

                // Reserve space on the right so text doesn't run under the dot
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        //blue dot pinned to the top-right corner of the card
        if (isUnread) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1E50FF),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .size(8.dp)
            ) {}
        }
    }
}