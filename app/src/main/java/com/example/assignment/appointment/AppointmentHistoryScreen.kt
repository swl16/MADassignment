package com.example.assignment.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.assignment.database.Appointment
import com.example.assignment.database.AppointmentDao
import com.example.assignment.database.UserDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentHistoryScreen(
    onBack: () -> Unit,
    onAppointmentClick: (Appointment) -> Unit = {},
    onBookAppointment: () -> Unit = {},
    appointmentDao: AppointmentDao? = null,
    username: String = "" // <-- NEW: Accept the username
) {
    // 1. Fetch data from DB
    val appointments by produceState<List<Appointment>>(initialValue = emptyList(), username) {
        appointmentDao?.getAppointmentsForUser(username)?.collect { value = it }
    }

    val upcoming = appointments.filter { it.status == "Upcoming" }
    val past = appointments.filter { it.status != "Upcoming" }

    Scaffold(
        containerColor = Color(0xFFF5FAFF),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5FAFF)),
                title = { 
                    Text(
                        "Appointment History", 
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1F38)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F1F38)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onBookAppointment,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1F6BE8))
                    ) {
                        Text("+ Book")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upcoming Section
            item {
                Text(
                    text = "Upcoming Appointments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1F38),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (upcoming.isEmpty()) {
                item { Text("No upcoming appointments", color = Color.Gray, fontSize = 14.sp) }
            } else {
                items(upcoming) { appointment ->
                    AppointmentHistoryCard(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) }
                    )
                }
            }

            // Past Section
            item {
                Text(
                    text = "Past Appointments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1F38),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (past.isEmpty()) {
                item { Text("No past appointments", color = Color.Gray, fontSize = 14.sp) }
            } else {
                items(past) { appointment ->
                    AppointmentHistoryCard(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) }
                    )
                }
            }

            // Secure Storage Message
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    color = Color(0xFFE5F2FF),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Your appointment history is stored securely.",
                            color = Color(0xFF1F6BE8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap an appointment to view its details.",
                            color = Color(0xFF63738C),
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AppointmentHistoryCard(
    appointment: Appointment,
    onClick: () -> Unit
) {
    // Extracting Month and Day from "Thursday, 16 July 2026"
    // This is a bit brittle, but works for the current format.
    val dateParts = appointment.date.split(" ")
    val day = dateParts.getOrNull(1) ?: ""
    val month = dateParts.getOrNull(2)?.take(3)?.uppercase() ?: ""

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shadowElevation = 0.dp // Usually cards in this design have minimal shadow or just white bg on off-white
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Box
            Surface(
                color = Color(0xFFE5EDFF),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(65.dp, 75.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = month, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(
                        0xFF1F6BE8
                    )
                    )
                    Text(text = day, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(
                        0xFF1F6BE8
                    )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.doctorName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1F38)
                )
                Text(
                    text = "${appointment.specialty} • ${appointment.time}",
                    fontSize = 13.sp,
                    color = Color(0xFF63738C)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (appointment.status == "Upcoming") "Confirmed" else "Completed",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (appointment.status == "Upcoming") Color(0xFF21A673) else Color(
                        0xFF1F6BE8
                    )
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF63738C)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentHistoryPreview() {
    // Provide an empty list or mock data for preview
    AppointmentHistoryScreen(onBack = {})
}
