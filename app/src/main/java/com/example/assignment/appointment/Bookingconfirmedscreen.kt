package com.example.assignment.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.Appointment
import com.example.assignment.database.AppointmentDao

@Composable
fun BookingConfirmedScreen(
    appointmentId: Int,
    appointmentDao: AppointmentDao,
    onViewDetails: () -> Unit = {},
    onBackToHome: () -> Unit = {}
) {
    var appointment by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(appointmentId) {
        appointment = appointmentDao.getById(appointmentId)
    }

    Scaffold(containerColor = Color(0xFFF8FAFF)) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E9E5B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirmed",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Appointment confirmed!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14213D)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your appointment has been booked successfully.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            appointment?.let { appt ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "APPOINTMENT DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E50FF)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(appt.doctorName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213D))
                    Text(appt.specialty, fontSize = 13.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(appt.date, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF14213D))
                    Text("${appt.time}  •  ${appt.location}", fontSize = 13.sp, color = Color.Gray)
                }
            } ?: CircularProgressIndicator(color = Color(0xFF1E50FF))

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFE5EDFF)),
                border = null
            ) {
                Text("View appointment details", color = Color(0xFF1E50FF), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
            ) {
                Text("Back to home", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}