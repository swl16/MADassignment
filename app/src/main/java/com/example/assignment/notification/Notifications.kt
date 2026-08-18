package com.example.assignment.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// IMPORTANT: This imports your custom top bar from the profile file!
import com.example.assignment.profile.StandardTopBar

@Composable
fun NotificationsScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        StandardTopBar(
            title = "Notifications",
            actionText = "Mark all read",
            onBackClick = onNavigateBack,
            onActionClick = { /* TODO: Hook up database logic later */ }
        )

        // Make the list scrollable in case there are a ton of notifications
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // --- TODAY SECTION ---
            Text(text = "Today", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78849E))
            Spacer(modifier = Modifier.height(12.dp))

            NotificationCard(
                icon = "✓", iconColor = Color.White, iconBg = Color(0xFF1E50FF),
                title = "Appointment confirmed",
                description = "Your visit with Dr. Sarah Lim is tomorrow at 10:30 AM.",
                time = "9:15 AM", isUnread = true
            )
            NotificationCard(
                icon = "!", iconColor = Color.White, iconBg = Color(0xFFFFA000), // Orange
                title = "Medication reminder",
                description = "It is time to take Amoxicillin after lunch.",
                time = "1:00 PM", isUnread = true
            )
            NotificationCard(
                icon = "📄", iconColor = Color.White, iconBg = Color(0xFF00BFA5), // Green
                title = "Record uploaded",
                description = "Your Blood Test Report is now available.",
                time = "Yesterday", isUnread = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- EARLIER SECTION ---
            Text(text = "Earlier", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78849E))
            Spacer(modifier = Modifier.height(12.dp))

            NotificationCard(
                icon = "", iconColor = Color.Transparent, iconBg = Color(0xFFE5EDFF), // Light blue empty circle
                title = "Clinic nearby",
                description = "A new panel clinic is available 0.8 km away.",
                time = "", isUnread = false
            )

            Spacer(modifier = Modifier.height(32.dp)) // Extra padding at the bottom so it doesn't hug the screen edge
        }
    }
}

@Composable
fun NotificationCard(
    icon: String, iconColor: Color, iconBg: Color,
    title: String, description: String, time: String,
    isUnread: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top // Ensures icon and dot align with the top line of text
        ) {
            // 1. The Circle Icon
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, color = iconColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. The Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 12.sp, color = Color.Gray, lineHeight = 18.sp)

                // Only show time if it's provided
                if (time.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = time, fontSize = 10.sp, color = Color(0xFFA0AABF))
                }
            }

            // 3. The Unread Blue Dot
            if (isUnread) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E50FF),
                    modifier = Modifier.size(8.dp).offset(y = 6.dp) // Pushes it down slightly to align with the title
                ) {}
            }
        }
    }
}