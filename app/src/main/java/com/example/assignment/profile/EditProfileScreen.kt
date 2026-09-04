package com.example.assignment.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.authentication.formatMalaysianMobile
import com.example.assignment.authentication.validateDateOfBirth
import com.example.assignment.authentication.validateEmail
import com.example.assignment.authentication.validateFullName
import com.example.assignment.authentication.validateMalaysianMobile
import com.example.assignment.ui.components.ProfilePicturePicker
import com.example.assignment.ui.theme.appTextFieldColors
import com.example.assignment.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun EditProfileScreen(
    viewModel: UserViewModel,
    username: String,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneField by remember { mutableStateOf(TextFieldValue("")) }
    var dob by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<String?>(null) }
    var showPicturePicker by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            // Disable future dates for Date of Birth
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            fullName = user.fullName
            email = user.email
            phoneField = TextFieldValue(user.mobileNumber)
            dob = user.dateOfBirth
            profilePictureUri = user.profilePictureUri
        }
    }

    val initials = fullName.take(2).uppercase().ifEmpty { "--" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        StandardTopBar(
            title = "Edit Profile",
            actionText = "",
            onBackClick = onNavigateBack,
            onActionClick = {}
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showPicturePicker = true }
            ) {
                ProfilePicturePicker(
                    currentImageUri = profilePictureUri,
                    initials = initials,
                    showDialog = showPicturePicker,
                    onDialogDismiss = { showPicturePicker = false },
                    onImageSelected = { uri -> profilePictureUri = uri.toString() },
                    size = 90.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Change Profile",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showPicturePicker = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileInputField(label = "Full name", value = fullName, onValueChange = { fullName = it })
            ProfileInputField(label = "Email address", value = email, onValueChange = { email = it })

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(text = "Phone number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = phoneField,
                    onValueChange = { input -> phoneField = formatMalaysianMobile(input) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = appTextFieldColors(Color(0xFFE5EDFF)),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    )
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(text = "Date of birth", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = dob,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("DD/MM/YYYY", color = Color(0xFF8DA6FF)) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                tint = Color(0xFF3B67E9)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = appTextFieldColors(Color(0xFFE5EDFF)),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Touch overlay to trigger date picker
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showDatePicker = true }
                    )
                }
            }

            //inline error message
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val error = validateFullName(fullName)
                        ?: validateEmail(email)
                        ?: validateMalaysianMobile(phoneField.text)
                        ?: validateDateOfBirth(dob)

                    if (error != null) {
                        errorMessage = error
                    } else {
                        errorMessage = ""
                        currentUser?.let { user ->
                            val updatedUser = user.copy(
                                fullName = fullName.trim(),
                                email = email.trim(),
                                mobileNumber = phoneField.text.trim(),
                                dateOfBirth = dob.trim(),
                                profilePictureUri = profilePictureUri // NEW
                            )
                            viewModel.updateUserProfile(updatedUser)
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
            ) {
                Text(text = "Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            dob = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFF1E50FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


}

@Composable
fun ProfileInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = appTextFieldColors(Color(0xFFE5EDFF)),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
