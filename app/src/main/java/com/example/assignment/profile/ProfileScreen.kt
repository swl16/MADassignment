package com.example.assignment.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.User
import com.example.assignment.database.UserDao

@Composable
fun ProfileScreen(
    userDao: UserDao,
    onNavigateBack: () -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
) {
    // 1. Create a blank state variable to hold the user data
    var currentUser by remember { mutableStateOf<User?>(null) }

    // 2. Fetch the user from the database as soon as the screen opens
    LaunchedEffect(Unit) {
        currentUser = userDao.getLatestUser()
    }

    // 3. Fallback text while it loads
    val displayName = currentUser?.fullName ?: "Loading..."
    val displayEmail = currentUser?.email ?: "Loading..."

    // Grab the first two letters of their name for the Avatar (e.g., "Wei Li" -> "WE")
    val initials = currentUser?.fullName?.take(2)?.uppercase() ?: "--"

    // Dynamically format the Room ID into a Healthcare ID (1 -> HC-2026-0001)
    val healthcareId = "HC-2026-${String.format("%04d", currentUser?.id ?: 0)}"
    // Add this to remember if the pop-up should be visible!
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        StandardTopBar(
            title = "Profile",
            actionText = "Edit",
            onBackClick = onNavigateBack,
            onActionClick = onNavigateToEdit
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Avatar
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color(0xFFE5EDFF),
                modifier = Modifier.size(90.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials, // Dynamic Initials
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E50FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Name and Email
            Text(
                text = displayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = displayEmail,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Healthcare ID Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E50FF)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "HEALTHCARE ID", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = healthcareId, // Dynamic ID
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(text = "Verified", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text(text = "Account & safety", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsRowItem(
                icon = "🕒", title = "Appointment history", subtitle = "Past and upcoming appointments", iconColor = Color.Gray,
                onClick = { /* TODO later */ } )
            SettingsRowItem(
                icon = "🔵", title = "Notification settings", subtitle = "Control alerts and reminders", iconColor = Color(0xFF1E50FF),
                onClick = { onNavigateToSettings() })
            SettingsRowItem(
                icon = "➕", title = "Emergency contact", subtitle = "Manage your trusted contact", iconColor = Color(0xFF1E50FF),
                onClick = { onNavigateToEmergency() })

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .height(50.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(text = "Log out", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            // 7. The Confirmation Pop-Up
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false }, // Closes if they tap outside the box
                    title = {
                        Text("Log out", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    },
                    text = {
                        Text("Are you sure you want to log out of your account?", color = Color.Gray)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false // Hide the dialog
                                onLogOut()               // ACTUALLY log them out!
                            }
                        ) {
                            Text("Yes, log out", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showLogoutDialog = false } // Just hide the dialog
                        ) {
                            Text("Cancel", color = Color(0xFF1E50FF), fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

// --- REUSABLE COMPONENTS GO AT THE BOTTOM ---

@Composable
fun StandardTopBar(
    title: String,
    actionText: String,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Back arrow + Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = onBackClick,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(24.dp)
            ) {
                Text("<", fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        // Right side: Action Text (Edit, Mark all read, etc)
        TextButton(
            onClick = onActionClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = actionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E50FF)
            )
        }
    }
}

@Composable
fun SettingsRowItem(
    icon: String,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Inner padding so text isn't hitting the edges
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Fake Icon using text for now
                Text(text = icon, fontSize = 18.sp, color = iconColor)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
                }
            }
            // Right Chevron Arrow
            Text(text = ">", fontSize = 16.sp, color = Color.LightGray)
        }
    }
}

