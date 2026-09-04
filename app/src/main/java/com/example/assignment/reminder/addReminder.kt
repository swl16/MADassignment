package com.example.assignment.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.example.assignment.database.Reminder
import com.example.assignment.database.ReminderViewModel

val AppBackground = Color(0xFFF4F7FB)
val PrimaryBlue = Color(0xFF246BFD)
val TextDark = Color(0xFF1A2138)
val TextGray = Color(0xFF8A93A6)
val OrangeWarning = Color(0xFFF9A826)
val GreenSuccess = Color(0xFF27AE60)
val RedDanger = Color(0xFFFF4B4B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isError) RedDanger else TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray) },
            isError = isError,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color(0xFFFFF0F0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = RedDanger
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = RedDanger,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isError) RedDanger else TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                placeholder = { Text("Select Time", color = TextGray) },
                isError = isError,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color(0xFFFFF0F0),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = RedDanger
                ),
                shape = RoundedCornerShape(12.dp),
                // menuAnchor is required to position the dropdown menu correctly
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }

        }

        if (isError) {
            Text(
                text = errorMessage,
                color = RedDanger,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun TopBar(title: String, onBackClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = TextDark
            )
        }
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun AddReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel, username: String
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }
    var instruction by remember { mutableStateOf("") }
    var notificationEnable by remember { mutableStateOf(true) }

    var nameError by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf(false) }

    val timeOptions = listOf(
        "8:00 AM",
        "8:30 AM",
        "9:00 AM",
        "9:30 AM",
        "10:00 AM",
        "10:30 AM",
        "11:00 AM",
        "11:30 AM",
        "12:00 PM",
        "12:30 PM",
        "1:00 PM",
        "1:30 PM",
        "2:00 PM",
        "2:30 PM",
        "3:00 PM",
        "3:30 PM",
        "4:00 PM",
        "4:30 PM",
        "5:00 PM",
        "5:30 PM",
        "6:00 PM",
        "6:30 PM",
        "7:00 PM",
        "7:30 PM",
        "8:00 PM",
        "8:30 PM",
        "9:00 PM",
        "9:30 PM",
        "10:00 PM",
        "10:30 PM",
        "11:00 PM",
        "11:30 PM",
        "12:00 AM"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {

        TopBar(title = "Add Reminder", onBackClick = { navController.popBackStack() })

        CustomTextField(
            value = medicineName,
            isError = nameError,
            errorMessage = "Medicine name is required",
            onValueChange = {
                medicineName = it
                nameError = false },
            label = "Medicine Name",
            placeholder = "e.g. Amoxicillin",
            modifier = Modifier.fillMaxWidth()

        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CustomTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = "Dosage",
                placeholder = "1 capsule",
                modifier = Modifier.weight(1f)
            )
            CustomTextField(
                value = frequency,
                onValueChange = { frequency = it },
                label = "Frequency",
                placeholder = "Once daily",
                modifier = Modifier.weight(1f)
            )
        }

        CustomDropdownField(
            label = "Reminder Time",
            selectedValue = reminderTime,
            options = timeOptions,
            isError = timeError,
            errorMessage = "Please select a time",
            onValueChange = {
                reminderTime = it
                timeError = false
            }
        )

        CustomTextField(
            value = instruction,
            onValueChange = { instruction = it },
            label = "Instructions",
            placeholder = "After dinner",
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Notification",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
                Text("Alert me at reminder time", color = TextGray, fontSize = 14.sp)
            }
            Switch(
                checked = notificationEnable,
                onCheckedChange = { notificationEnable = it },
                colors = SwitchDefaults.colors(checkedTrackColor = PrimaryBlue)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val isNameValid = medicineName.isNotBlank()
                val isTimeValid = reminderTime.isNotBlank()

                nameError = !isNameValid
                timeError = !isTimeValid

                if(isNameValid && isTimeValid) {
                    val newReminder = Reminder(
                        medicineName = medicineName.trim(),
                        dosage = dosage.trim(),
                        frequency = frequency.trim(),
                        time = reminderTime,
                        instructions = instruction.trim(),
                        isNotificationEnabled = notificationEnable,
                        isActive = true
                    )
                    viewModel.saveReminder(newReminder, username)
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(PrimaryBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}