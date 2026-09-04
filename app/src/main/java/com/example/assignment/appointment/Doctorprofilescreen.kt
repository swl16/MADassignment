package com.example.assignment.appointment

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorProfileScreen(
    doctor: Doctor,
    onNavigateBack: () -> Unit = {},
    onBookAppointment: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(doctor.availableTimes.firstOrNull()) }

    val context = LocalContext.current

    Scaffold(containerColor = Color(0xFFF8FAFF)) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF14213D),
                    modifier = Modifier.clickable { onNavigateBack() }
                )

                Text(
                    text = "Doctor Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF14213D)
                )

                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color(0xFF1E50FF),
                    modifier = Modifier.clickable { isFavorite = !isFavorite }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Header card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1E50FF))
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5EDFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = doctor.initials,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E50FF)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = doctor.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = doctor.specialty, fontSize = 13.sp, color = Color(0xFFE5EDFF))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(value = "${doctor.rating}", label = "Rating", modifier = Modifier.weight(1f))
                StatCard(value = "${doctor.experienceYears} yrs", label = "Experience", modifier = Modifier.weight(1f))
                StatCard(value = doctor.patientsCount, label = "Patients", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "About", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213D))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = doctor.about, fontSize = 13.sp, color = Color.Gray, lineHeight = 19.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Available times", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213D))
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                doctor.availableTimes.forEach { time ->
                    TimeSlotChip(
                        label = time,
                        selected = selectedTime == time,
                        onClick = { selectedTime = time }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBookAppointment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
            ) {
                Text("Book appointment", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    val clinicNumber = "0123456789" // Clinic contact number
                    val inquiryMessage = "Hello, I would like to inquire about an appointment with ${doctor.name} at ${doctor.location}."

//                    val url = "https://api.whatsapp.com/send?phone=$clinicNumber&text=${java.net.URLEncoder.encode(inquiryMessage, "UTF-8")}"
//
//                    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
//                        data = Uri.parse(url)
//                    }

                    val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("sms:$clinicNumber")
                        putExtra("sms_body", inquiryMessage)
                    }

                    try {
                        context.startActivity(smsIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No SMS application available on this device", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text("Message clinic", color = Color(0xFF1E50FF), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213D))
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
private fun TimeSlotChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0xFF1E50FF) else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else Color(0xFF14213D)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorProfilePreview() {
    DoctorProfileScreen(doctor = sampleDoctors[0])
}