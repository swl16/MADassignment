package com.example.assignment.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.User
import com.example.assignment.database.UserDao
import com.example.assignment.ui.theme.appTextFieldColors
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    userDao: UserDao,
    username: String, // <-- NEW: Accept the username
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf<User?>(null) }

    // State variables for the text fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    // Fetch the data as soon as the screen opens and populate the text fields!
    LaunchedEffect(username) {
        val user = userDao.getUserByUsername(username)
        if (user != null) {
            currentUser = user
            fullName = user.fullName
            email = user.email
            phone = user.mobileNumber
            dob = user.dateOfBirth
        }
    }

    val initials = fullName.take(2).uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        // We reuse your StandardTopBar, but leave the actionText blank!
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

            // Avatar
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

            Text(
                text = "Change photo",
                color = Color(0xFF1E50FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* TODO later */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Custom Text Fields matching Figma
            ProfileInputField(label = "Full name", value = fullName, onValueChange = { fullName = it })
            ProfileInputField(label = "Email address", value = email, onValueChange = { email = it })
            ProfileInputField(label = "Phone number", value = phone, onValueChange = { phone = it })
            ProfileInputField(label = "Date of birth", value = dob, onValueChange = { dob = it })

            Spacer(modifier = Modifier.weight(1f))

            // Save Changes Button
            Button(
                onClick = {
                    scope.launch {
                        currentUser?.let { user ->
                            // Create an updated user object keeping the same exact ID
                            val updatedUser = user.copy(
                                fullName = fullName,
                                email = email,
                                mobileNumber = phone,
                                dateOfBirth = dob
                            )
                            userDao.updateUser(updatedUser)
                            onNavigateBack() // Go back to profile after saving
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

// Reusable custom text field to perfectly match the white Figma cards!
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