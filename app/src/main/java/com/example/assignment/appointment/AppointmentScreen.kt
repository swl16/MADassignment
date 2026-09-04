package com.example.assignment.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignment.navigation.BottomNavBar

// Doctor data class + sampleDoctors now live in DoctorData.kt (shared with DoctorProfileScreen)

private val specialties = listOf("All","General", "Dental", "Cardiology", "Pediatrics", "Neurology", "More")

// ---------- Screen ----------

@Composable
fun AppointmentMain(
    navController: NavController,
    onBookNow: (Doctor) -> Unit = {},
    onViewProfile: (Int) -> Unit = {} // index into sampleDoctors, passed as navArgument
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecialty by remember { mutableStateOf("All") }

    val filteredDoctors = remember(selectedSpecialty, sampleDoctors) {
        if (selectedSpecialty == "All") {
            sampleDoctors
        } else {
            sampleDoctors.filter { it.specialty.equals(selectedSpecialty, ignoreCase = true) }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFF),
        bottomBar = { BottomNavBar(navController = navController, selectedIndex = 1) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Book Appointment",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14213D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search doctor or specialty", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Choose specialty",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14213D)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(specialties) { specialty ->
                    SpecialtyChip(
                        label = specialty,
                        selected = selectedSpecialty == specialty,
                        onClick = { selectedSpecialty = specialty }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Available doctors",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14213D)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredDoctors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No doctors found in this specialty",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredDoctors) { doctor ->
                        // look up the doctor's real position in the master list, since filtering changes index
                        val realIndex = sampleDoctors.indexOf(doctor)
                        DoctorCard(
                            doctor = doctor,
                            onViewProfile = { onViewProfile(realIndex) },
                            onBookNow = { onBookNow(doctor) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date / Location bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Date: 15/7/2026", fontSize = 13.sp, color = Color(0xFF14213D))
                Text(
                    text = "Location: Nearby  ›",
                    fontSize = 13.sp,
                    color = Color(0xFF1E50FF),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SpecialtyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Color(0xFF1E50FF) else Color.White)
//            .then(Modifier.padding(horizontal = 18.dp, vertical = 10.dp)),
            .border(
                width = 1.dp,
                color = if (selected) Color(0xFF1E50FF) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(50)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else Color(0xFF14213D),
//            modifier = Modifier.clickableNoRipple(onClick)
        )
    }
}

// small helper so the chip text itself is clickable without pulling in extra deps
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            onClick = onClick
        )
    )

@Composable
private fun DoctorCard(doctor: Doctor, onViewProfile: () -> Unit, onBookNow: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5EDFF))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(text = doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213D))
                Text(text = doctor.specialty, fontSize = 13.sp, color = Color.Gray)
                Text(
                    text = "★ ${doctor.rating}  •  ${doctor.availability}",
                    fontSize = 12.sp,
                    color = Color(0xFF1E50FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onViewProfile,
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFE5EDFF)),
                border = null,
                modifier = Modifier.weight(1f)
            ) {
                Text("View profile", color = Color(0xFF1E50FF), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onBookNow,
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Book now", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentPreview() {
    AppointmentMain(navController = rememberNavController())
}