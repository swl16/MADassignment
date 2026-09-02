package com.example.assignment.authentication

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.components.PasswordTextField
import com.example.assignment.ui.theme.appTextFieldColors
import com.example.assignment.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
    var mobileNumber by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }

    // NEW: Local validation error, separate from the ViewModel's server/db error
    var validationError by remember { mutableStateOf("") }

    // NEW: Which dialog (if any) is currently showing
    var activeDialog by remember { mutableStateOf<LegalDialog?>(null) }

    val viewModelError by viewModel.errorMessage.collectAsState()
    // Show whichever error is more recent: prioritize local validation feedback
    val displayedError = if (validationError.isNotEmpty()) validationError else viewModelError

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
            value = mobileNumber,
            onValueChange = { input ->
                mobileNumber = formatMalaysianMobile(input)
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
        TextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            placeholder = { Text(" DD/MM/YYYY ", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF)),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(30.dp))

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
                }
                Row {
                    Text(
                        text = "and ",
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
                    mobileNumber = mobileNumber,
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
                        mobileNumber = mobileNumber.trim(),
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

        Spacer(modifier = Modifier.height(40.dp))

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
    val dateValidationResult = if (dateOfBirth.isNotBlank()) validateDateLogic(dateOfBirth.trim()) else null

    return when {
        // --- 1. Username Validation ---
        username.isBlank() -> "Username is required"
        username.trim().length < 3 -> "Username must be at least 3 characters"
        username.contains(" ") -> "Username cannot contain spaces"
        !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Username can only contain letters, numbers, and underscores"

        // --- 2. Full Name Validation ---
        fullName.isBlank() -> "Full name is required"
        fullName.trim().length < 2 -> "Full name is too short"
        !fullName.matches(Regex("^[a-zA-Z\\s'.-]+$")) -> "Name can only contain letters, spaces, hyphens, or apostrophes"

        // --- 3. Password Validation ---
        passwordError != null -> passwordError

        // --- 4. Email Validation ---
        email.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Please enter a valid email address"

        // --- 5. Mobile Number Validation ---
        mobileNumber.isBlank() -> "Mobile number is required"
        !mobileNumber.matches(Regex("^01[0-9]-[0-9]{7,8}$")) ->
            "Please enter a valid Malaysian mobile number (e.g. 012-3456789)"

        // --- 6. Date of Birth Validation ---
        dateOfBirth.isBlank() -> "Date of birth is required"
        !dateOfBirth.trim().matches(Regex("^\\d{2}/\\d{2}/\\d{4}$")) ->
            "Date of birth must be in DD/MM/YYYY format"
        dateValidationResult != null -> dateValidationResult

        // --- 7. Terms & Conditions ---
        !agreedToTerms -> "You must agree to the Terms of Use and Privacy Policy"

        else -> null
    }
}

/**
 * Formats raw digit input into Malaysian mobile format.
 * Most prefixes: 01X-XXXXXXX (10 digits total, e.g. 012-3456789)
 * 011 prefix:    011-XXXXXXXX (11 digits total, e.g. 011-12345678)
 */
private fun formatMalaysianMobile(input: String): String {
    val digits = input.filter { it.isDigit() }

    // 011 numbers get an extra digit before the dash; everything else is 3-then-rest
    val prefixLength = if (digits.startsWith("011")) 3 else 3
    val maxLength = if (digits.startsWith("011")) 11 else 10

    val capped = digits.take(maxLength)

    return if (capped.length <= prefixLength) {
        capped
    } else {
        "${capped.substring(0, prefixLength)}-${capped.substring(prefixLength)}"
    }
}

private fun validateDateLogic(dateStr: String): String? {
    return try {
        // Enforce strict parsing
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parsedDate = LocalDate.parse(dateStr, formatter)
        val today = LocalDate.now()

        when {
            parsedDate.isAfter(today) -> "Date of birth cannot be in the future"
            // Optional: Prevent them from being 150 years old
            parsedDate.isBefore(today.minusYears(130)) -> "Please enter a valid year"
            else -> null // Date is valid
        }
    } catch (e: DateTimeParseException) {
        "Please enter a valid calendar date (e.g., 31/12/1990)"
    }
}