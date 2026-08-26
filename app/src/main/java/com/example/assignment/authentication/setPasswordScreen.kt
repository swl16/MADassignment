package com.example.assignment.authentication

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.UserDao
import com.example.assignment.database.hashPassword
import com.example.assignment.ui.theme.appTextFieldColors
import kotlinx.coroutines.launch

@Composable
fun SetPasswordScreen(
    userDao: UserDao? = null, // null = design/preview mode, just calls onPasswordCreated without saving
    username: String = "",    // <-- NEW: Accept the username
    onBackClick: () -> Unit = {},
    onPasswordCreated: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

// THE HEADER (Back Arrow + Centered Title)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // 1. Create a blank interaction source to swallow the ripple effect
            val interactionSource = remember { MutableInteractionSource() }

            Text(
                text = "<",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E50FF),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    // 2. The magic fix: indication = null completely removes the grey box!
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onBackClick
                    )
                    .padding(8.dp)
            )

            Text(
                text = "Set Password",
                fontSize = 22.sp,
                fontWeight = SemiBold,
                color = Color(0xFF1E50FF)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create a secure new password for your HealthCare account.",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // MAIN PASSWORD FIELD
        Text(
            text = "Password",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("********", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CONFIRM PASSWORD FIELD
        Text(
            text = "Confirm Password",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("********", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )

        Spacer(modifier = Modifier.height(40.dp))

        // THE GHOST BOX (Error Message)
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // SUBMIT BUTTON & LOGIC
        Button(
            onClick = {
                if (password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "Please fill in all fields"
                } else if (password.length < 6) {
                    errorMessage = "Password must be at least 6 characters"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    errorMessage = ""
                    if (userDao != null) {
                        scope.launch {
                            val currentUser = userDao.getUserByUsername(username)
                            if (currentUser != null) {
                                userDao.updateUser(currentUser.copy(password = hashPassword(password)))
                            }
                            onPasswordCreated()
                        }
                    } else {
                        onPasswordCreated()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
        ) {
            Text(
                text = "Create New Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SetPasswordScreenPreview() {
    SetPasswordScreen()
}