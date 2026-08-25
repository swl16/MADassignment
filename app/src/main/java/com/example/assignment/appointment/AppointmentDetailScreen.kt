package com.example.assignment.appointment


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignment.database.Appointment

@Composable
fun AppointmentDetailScreen(
    navController: NavController,
    appointment: Appointment?,doctor: Doctor,
    onCancelAppointment: (Appointment) -> Unit = {}
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF5FAFF),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F1F38),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Appointment Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1F38)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            if (appointment != null) {
                // Status Banner
                val statusColor = if (appointment.status == "Upcoming") Color(0xFF21A673) else Color(
                    0xFF1F6BE8
                )
                Surface(
                    color = Color(0xFFE5F2FF),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appointment.status,
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "APPOINTMENT ID: APT-${appointment.id.toString().padStart(4, '0')}",
                            color = Color(0xFF63738C),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Doctor Card
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Initials Circle
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5F2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = appointment.doctorName.split(" ")
                                .filter { it.isNotEmpty() }
                                .take(2)
                                .joinToString("") { it.take(1) }
                            Text(
                                text = initials,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F6BE8)
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            Text(
                                text = "Dr. ${appointment.doctorName}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1F38)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = appointment.specialty,
                                fontSize = 14.sp,
                                color = Color(0xFF63738C)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF1F6BE8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${doctor.rating} • ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F6BE8)
                                )
                                Text(
                                    text = appointment.location,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F6BE8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Appointment Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1F38)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info Rows
                InfoRow(label = "Date", value = appointment.date)
                InfoRow(label = "Time", value = appointment.time)
                InfoRow(label = "Location", value = appointment.location)
                InfoRow(label = "Reason", value = appointment.reason)

                Spacer(modifier = Modifier.weight(1f))

                // Buttons
                Button(
                    onClick = { navController.navigate("reschedule_appointment/${appointment.id}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6BE8))
                ) {
                    Text("Reschedule Appointment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53847)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53847))
                ) {
                    Text("Cancel Appointment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Appointment details not found")
                }
            }
        }
    }

    if (showCancelDialog && appointment != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Appointment") },
            text = { Text("Are you sure you want to cancel your appointment with Dr. ${appointment.doctorName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancelAppointment(appointment)
                        navController.popBackStack()
                    }
                ) {
                    Text("Yes, Cancel", color = Color(0xFFE53847), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Appointment")
                }
            }
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.width(80.dp),
                fontSize = 13.sp,
                color = Color(0xFF63738C)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F1F38)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentDetailPreview() {
    // Provide an empty list or mock data for preview
    AppointmentDetailScreen(navController = rememberNavController(),Appointment(1,2001,"Sarah Lim", "General", "10/10/2026","10:30","HealthCare Clinic","Upcoming","General reason"), sampleDoctors[1])
}
