package com.example.assignment.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.theme.appTextFieldColors
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    userDao: com.example.assignment.database.UserDao,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }

    // NEW: Error message state
    var errorMessage by remember { mutableStateOf("") }

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

        Text(
            text = "Username",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("eg.John", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Full Name",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = { Text("eg.John", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )

        Spacer(modifier = Modifier.height(10.dp))

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
            visualTransformation = PasswordVisualTransformation(), // Added this so it hides text!
            placeholder = { Text("********", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Email",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("eg. example123@example.com", color = Color(0xFF8DA6FF)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Mobile Number",
            fontSize = 12.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            placeholder = { Text("+60 12-3456789", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Date of Birth ",
            fontSize = 12.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = dateOfBirth, // FIXED: Was 'mobileNumber' before!
            onValueChange = { dateOfBirth = it },
            placeholder = { Text(" DD/MM/YY ", color = Color(0xFF8DA6FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = appTextFieldColors(Color(0xFFE5EDFF))
        )
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "By continuing, please agree to our ",
            fontSize = 11.sp,
            fontWeight = SemiBold,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Terms of use and Privacy Policy",
            fontSize = 10.sp,
            fontWeight = SemiBold,
            color = Color(0xFF1E50FF),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // NEW: Display the error message right above the button
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

        Button(
            // NEW: Validation Logic
            onClick = {
                if (fullName.isBlank() || password.isBlank() || email.isBlank() || mobileNumber.isBlank() || dateOfBirth.isBlank()) {
                    errorMessage = "Please fill in all fields"
                } else if (!email.contains("@")) {
                    errorMessage = "Please enter a valid email address"
                } else if (password.length < 6) {
                    errorMessage = "Password must be at least 6 characters"
                } else {
                    // Start a background thread
                    scope.launch {
                        // 1. Check if email exists
                        val existingUser = userDao.getUserByEmail(email)
                        val existingUsername = userDao.getUserByUsername(username)

                        if (existingUser != null) {
                            errorMessage = "Email is already registered!"
                        } else if (existingUsername != null) {
                            errorMessage = "Username is already used by others!"
                        } else {
                            // 2. Create the User object — password stored as a hash, never plaintext
                            val newUser = com.example.assignment.database.User(
                                username = username,
                                fullName = fullName,
                                email = email,
                                password = com.example.assignment.database.hashPassword(password),
                                mobileNumber = mobileNumber,
                                dateOfBirth = dateOfBirth
                            )
                            // 3. Save to database and navigate!
                            userDao.insertUser(newUser)
                            errorMessage = ""
                            onNavigateToHome(username)
                        }
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
}