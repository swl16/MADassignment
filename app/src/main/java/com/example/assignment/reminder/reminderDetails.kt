package com.example.assignment.reminder

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.assignment.database.Reminder
import com.example.assignment.database.ReminderViewModel

@Composable
fun ReminderDetailsScreen(
    navController: NavController,
    viewModel: ReminderViewModel,
    reminderId: String, username: String
) {
    // Collect the list of reminders from the ViewModel
    val reminders by viewModel.reminders.collectAsState()

    // UI State for inputs
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var notificationEnabled by remember { mutableStateOf(true) }
    var isActive by remember { mutableStateOf(true) }

    var nameError by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf(false) }

    val timeOptions = listOf("8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
        "11:00 AM","11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
        "3:00 PM","3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM","7:00 PM",
        "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM", "10:30 PM", "11:00 PM", "11:30 PM", "12:00 AM")

    // When the screen loads, find the reminder and populate the fields
    LaunchedEffect(reminderId, reminders) {
        val existingReminder = reminders.find { it.id == reminderId }
        if (existingReminder != null) {
            medicineName = existingReminder.medicineName
            dosage = existingReminder.dosage
            frequency = existingReminder.frequency
            reminderTime = existingReminder.time
            instructions = existingReminder.instructions
            notificationEnabled = existingReminder.isNotificationEnabled
            isActive = existingReminder.isActive
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FB))
            .padding(20.dp)
            .statusBarsPadding()
    ) {
        TopBar(title = "Reminder Details", onBackClick = { navController.popBackStack() })

        // Active Status Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8F1FC), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF27AE60), shape = RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isActive) "Active reminder" else "Inactive reminder",
                    color = Color(0xFF27AE60),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Medicine name",
            value = medicineName,
            isError = nameError,
            errorMessage = "Medicine name is required",
            onValueChange = {
                medicineName = it
                nameError = false }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CustomTextField(label = "Dosage", value = dosage, onValueChange = { dosage = it }, modifier = Modifier.weight(1f))
            CustomTextField(label = "Frequency", value = frequency, onValueChange = { frequency = it }, modifier = Modifier.weight(1f))
        }

        CustomDropdownField(
            label = "Reminder time",
            selectedValue = reminderTime,
            options = timeOptions,
            isError = timeError,
            errorMessage = "Please select a time",
            onValueChange = {
                reminderTime = it
                timeError = false
            }
        )

        CustomTextField(label = "Instructions", value = instructions, onValueChange = { instructions = it })

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Notification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Alert me when the dose is due", color = Color.Gray, fontSize = 14.sp)
            }
            Switch(
                checked = notificationEnabled,
                onCheckedChange = { notificationEnabled = it },
                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF246BFD))
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save Changes Button
        Button(
            onClick = {
                val isNameValid = medicineName.isNotBlank()
                val isTimeValid = reminderTime.isNotBlank()

                nameError = !isNameValid
                timeError = !isTimeValid
                // Update the existing reminder object
                if(isNameValid && isTimeValid) {
                    val updatedReminder = Reminder(
                        id = reminderId, // Keep the same document ID to update, not create new
                        medicineName = medicineName,
                        dosage = dosage,
                        frequency = frequency,
                        time = reminderTime,
                        instructions = instructions,
                        isNotificationEnabled = notificationEnabled,
                        isActive = isActive
                    )
                    viewModel.saveReminder(updatedReminder, username)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF246BFD)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Delete Button
        OutlinedButton(
            onClick = {
                viewModel.deleteReminder(reminderId)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4B4B)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4B4B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Delete Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}