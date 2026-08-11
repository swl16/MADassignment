package com.example.assignment.homescreen

// 1. Clean, explicit imports so Android Studio doesn't get confused
import android.R.attr.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFF8FAFF),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text(text = "⌂", fontSize = 24.sp, color = if (selectedTab == 0) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Home", fontSize = 10.sp, color = if (selectedTab == 0) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text(text = "🕒", fontSize = 20.sp, color = if (selectedTab == 1) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Appointments", fontSize = 10.sp, color = if (selectedTab == 1) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text(text = "⏰", fontSize = 20.sp, color = if (selectedTab == 2) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Reminders", fontSize = 10.sp, color = if (selectedTab == 2) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Text(text = "☰", fontSize = 20.sp, color = if (selectedTab == 3) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Records", fontSize = 10.sp, color = if (selectedTab == 3) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
    ) { innerPadding: PaddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopHeader()
            GreetingLayer()
        }
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. The Brand Name
        Text(
            text = "HealthCare",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E50FF)
        )

        // 2. The Icons Group
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Puts a gap between the two circles
        ) {
            // Notification Bell Background
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color(0xFFF0F5FF),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Using a unicode bell so we don't need the icon library!
                    Text("🔔", fontSize = 16.sp)
                }
            }

            // Profile Badge Background
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color(0xFFE5EDFF),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "WL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E50FF)
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingLayer(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ){
        Text(
            text = "Hello, User",
            fontSize = 27.sp,
            fontWeight = SemiBold,
        )
        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = "How can we help you today? ",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}