package com.example.assignment.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.assignment.ui.theme.appTextFieldColors
import com.example.assignment.viewmodel.UserViewModel

@Composable
fun EditProfileScreen(
    viewModel: UserViewModel,
    username: String,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneField by remember { mutableStateOf(TextFieldValue("")) } // CHANGED: cursor-safe
    var dob by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") } // NEW

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            fullName = user.fullName
            email = user.email
            phoneField = TextFieldValue(user.mobileNumber) // CHANGED
            dob = user.dateOfBirth
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
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFE5EDFF),
                modifier = Modifier.size(90.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = initials, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E50FF))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Spacer(modifier = Modifier.height(32.dp))

            ProfileInputField(label = "Full name", value = fullName, onValueChange = { fullName = it })
            ProfileInputField(label = "Email address", value = email, onValueChange = { email = it })

            // CHANGED: phone number now uses cursor-safe formatted field
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(text = "Phone number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = phoneField,
                    onValueChange = { input -> phoneField = formatMalaysianMobile(input) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = appTextFieldColors(Color.White),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    )
                )
            }

            ProfileInputField(label = "Date of birth", value = dob, onValueChange = { dob = it })

            // NEW: inline error message
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
                                dateOfBirth = dob.trim()
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
            colors = appTextFieldColors(Color.White),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
