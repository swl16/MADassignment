package com.example.assignment.appointment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// NOTE: java.time requires core library desugaring for minSdk < 26.
// In app-level build.gradle:
//   android { compileOptions { isCoreLibraryDesugaringEnabled = true } }
//   dependencies { coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4") }

private val timeSlots = listOf("9:00 AM", "10:30 AM", "2:00 PM", "3:30 PM")

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectDateTimeScreen(
    doctor: Doctor,
    initialDate: LocalDate = LocalDate.now(),
    initialTime: String? = null,
    isRescheduling: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onConfirm: (date: LocalDate, time: String) -> Unit = { _, _ -> }
) {
    var weekStart by remember {
        mutableStateOf(initialDate.with(DayOfWeek.MONDAY))
    }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedTime by remember { mutableStateOf<String?>(initialTime) }

    val weekDays = remember(weekStart) { (0..6).map { weekStart.plusDays(it.toLong()) } }

    Scaffold(containerColor = Color(0xFFF8FAFF)) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF14213D),
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isRescheduling) "Reschedule Appointment" else "Select Date & Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF14213D)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Doctor summary row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5EDFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(doctor.initials, color = Color(0xFF1E50FF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(doctor.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213D))
                    Text(doctor.specialty, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Month header + week navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weekStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF14213D)
                )
                Row {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous week",
                        tint = Color.Gray,
                        modifier = Modifier.clickable { weekStart = weekStart.minusWeeks(1) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next week",
                        tint = Color.Gray,
                        modifier = Modifier.clickable { weekStart = weekStart.plusWeeks(1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Week row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDays.forEach { day ->
                    DayChip(
                        day = day,
                        selected = day == selectedDate,
                        onClick = { selectedDate = day }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Available time", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213D))
            Spacer(modifier = Modifier.height(10.dp))

            // 2x2 time slot grid
            timeSlots.chunked(2).forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowSlots.forEach { slot ->
                        TimeSlotButton(
                            label = slot,
                            selected = selectedTime == slot,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTime = slot }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Appointment summary
            if (selectedTime != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5EDFF))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Appointment summary",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E50FF)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF14213D)
                    )
                    Text(
                        text = "$selectedTime  •  HealthCare Clinic",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = { selectedTime?.let { onConfirm(selectedDate, it) } },
                enabled = selectedTime != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
            ) {
                Text(text = if (isRescheduling) "Confirm Reschedule" else "Confirm appointment",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayChip(day: LocalDate, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase().take(3),
            fontSize = 10.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (selected) Color(0xFF1E50FF) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${day.dayOfMonth}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else Color(0xFF14213D)
            )
        }
    }
}

@Composable
private fun TimeSlotButton(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0xFF1E50FF) else Color.White)
            .clickable { onClick() }
            .padding(vertical = 14.dp),
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SelectDateTimePreview() {
    SelectDateTimeScreen(doctor = sampleDoctors[0],isRescheduling = true,
        onNavigateBack = {},
        onConfirm = { _, _ -> })
}