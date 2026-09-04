package com.example.assignment.authentication

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.components.PasswordTextField
import com.example.assignment.ui.theme.appTextFieldColors
import com.example.assignment.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun SignUpScreen(
    viewModel: UserViewModel,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: (String) -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumberField by remember { mutableStateOf(TextFieldValue("")) }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateOfBirth by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var agreedToTerms by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf("") }
    var activeDialog by remember { mutableStateOf<LegalDialog?>(null) }
    val viewModelError by viewModel.errorMessage.collectAsState()
    val displayedError = if (validationError.isNotEmpty()) validationError else viewModelError

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "New Account",
            fontSize = 22.sp,
            fontWeight = SemiBold,
            color = Color(0xFF1E50FF),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Username", fontSize = 14.sp, fontWeight = SemiBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("eg.John", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF)),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Full Name", fontSize = 14.sp, fontWeight = SemiBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = { Text("eg.John", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF)),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Password", fontSize = 14.sp, fontWeight = SemiBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Email", fontSize = 14.sp, fontWeight = SemiBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("eg. example123@example.com", color = Color(0xFF8DA6FF)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = appTextFieldColors(Color(0xFFE5EDFF)),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Mobile Number", fontSize = 12.sp, fontWeight = SemiBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value = mobileNumberField,
            onValueChange = { input ->
                mobileNumberField = formatMalaysianMobile(input)
            },
            placeholder = { Text("012-3456789", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF)),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Date of Birth ", fontSize = 12.sp, fontWeight = SemiBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                value = dateOfBirth,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(" DD/MM/YYYY ", color = Color(0xFF8DA6FF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(Color(0xFFE5EDFF)),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = Color(0xFF3B67E9)
                    )
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        // 2. Material 3 Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                // Convert UTC timestamp to readable date string
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                                    timeZone = TimeZone.getTimeZone("UTC")
                                }
                                dateOfBirth = formatter.format(Date(millis))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // ---- TERMS & PRIVACY SECTION ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = { agreedToTerms = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E50FF))
            )
            Spacer(modifier = Modifier.height(1.dp)) // keeps Row height stable across recompositions
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Row {
                    Text(
                        text = "I agree to the ",
                        fontSize = 11.sp,
                        fontWeight = SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Terms of Use",
                        fontSize = 11.sp,
                        fontWeight = SemiBold,
                        color = Color(0xFF1E50FF),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { activeDialog = LegalDialog.TERMS }
                    )
                    Text(
                        text = " and ",
                        fontSize = 11.sp,
                        fontWeight = SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Privacy Policy",
                        fontSize = 11.sp,
                        fontWeight = SemiBold,
                        color = Color(0xFF1E50FF),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { activeDialog = LegalDialog.PRIVACY }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (displayedError.isNotEmpty()) {
            Text(
                text = displayedError,
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val error = validateSignUpForm(
                    username = username,
                    fullName = fullName,
                    password = password,
                    email = email,
                    mobileNumber = mobileNumberField.text,
                    dateOfBirth = dateOfBirth,
                    agreedToTerms = agreedToTerms
                )

                if (error != null) {
                    validationError = error
                } else {
                    validationError = ""
                    viewModel.clearError()

                    val newUser = com.example.assignment.database.User(
                        username = username.trim(),
                        fullName = fullName.trim(),
                        email = email.trim(),
                        password = com.example.assignment.database.hashPassword(password),
                        mobileNumber = mobileNumberField.text.trim(),
                        dateOfBirth = dateOfBirth.trim()
                    )
                    viewModel.signUp(newUser) { savedUsername ->
                        onNavigateToHome(savedUsername)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .size(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
        ) {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 11.sp,
                fontWeight = SemiBold,
                color = Color.Gray
            )
            TextButton(
                onClick = onNavigateToLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 11.sp,
                    fontWeight = SemiBold,
                    color = Color(0xFF1E50FF)
                )
            }
        }
    }

    // ---- DIALOGS ----
    if (activeDialog != null) {
        LegalContentDialog(
            type = activeDialog!!,
            onDismiss = { activeDialog = null }
        )
    }
}

private enum class LegalDialog { TERMS, PRIVACY }

@Composable
private fun LegalContentDialog(type: LegalDialog, onDismiss: () -> Unit) {
    val title = if (type == LegalDialog.TERMS) "Terms of Use" else "Privacy Policy"
    val body = if (type == LegalDialog.TERMS) {
        "By creating an account with HealthCare, you agree to use this app responsibly and " +
                "only for its intended purpose of managing your personal health information, " +
                "appointments, and reminders. You are responsible for keeping your login " +
                "credentials confidential. Misuse of the platform, including attempts to access " +
                "other users' data, may result in account suspension."
    } else {
        "HealthCare collects the personal information you provide (name, email, mobile " +
                "number, date of birth) solely to create and manage your account, schedule " +
                "appointments, and send reminders. Your data is stored securely and is not sold " +
                "to third parties. You may request access to, correction of, or deletion of your " +
                "data at any time through your profile settings."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF1E50FF))
        },
        text = {
            Text(
                text = body,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color.DarkGray,
                modifier = Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState())
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF1E50FF), fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Validates every SignUpScreen field. Returns a user-facing error message,
 * or null if the form is valid and ready to submit.
 */
private fun validateSignUpForm(
    username: String,
    fullName: String,
    password: String,
    email: String,
    mobileNumber: String,
    dateOfBirth: String,
    agreedToTerms: Boolean
): String? {
    val passwordError = validatePassword(password)
    val fullNameError = validateFullName(fullName)
    val emailError = validateEmail(email)
    val mobileError = validateMalaysianMobile(mobileNumber)
    val dobError = validateDateOfBirth(dateOfBirth)

    return when {
        username.isBlank() -> "Username is required"
        username.trim().length < 3 -> "Username must be at least 3 characters"
        username.contains(" ") -> "Username cannot contain spaces"
        !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Username can only contain letters, numbers, and underscores"

        fullNameError != null -> fullNameError
        passwordError != null -> passwordError
        emailError != null -> emailError
        mobileError != null -> mobileError
        dobError != null -> dobError

        !agreedToTerms -> "You must agree to the Terms of Use and Privacy Policy"

        else -> null
    }
}
