package com.example.assignment.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem

@Composable
fun Reminder() {
    Scaffold(bottomBar = {BottomNavigation()}, containerColor = Color(0xFFF7F8FA)){
        paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)){
            Spacer(modifier = Modifier.height(24.dp))

            //Header
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){

                Text(text = "Medication",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )

                IconButton(
                    onClick = {/* handle add reminder navigation*/},
                    modifier = Modifier.background(Color(0xFF2563EB), CircleShape).size(40.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Reminder",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))



        }
    }

}

@Composable
fun BottomNavigation(){
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ){
        NavigationBarItem(
            icon = {Icon(icons.Default.Home, contentDescription = "Home")},
            label = {Text("Home", fontSize = 10.sp)},
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = {Icon(icons.Default.DataRange, contentDescription = "Appointments")},
            label = {Text("Appointments", fontSize = 10.sp)},
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = {Icon(icons.Default.Notifications, contentDescription = "Reminders")},
            label = {Text("Reminders", fontSize = 10.sp)},
            selected = true,
            colors = NavigationBarDefaults.colors(selectedIconColor = Color(0xFF2563EB), selectedTextColor = Color(0xFF2563EB), indicatorColor = Color.White),
            onClick = { }
        )
        NavigationBarItem(
            icon = {Icon(icons.Default.List, contentDescription = "Records")},
            label = {Text("Records", fontSize = 10.sp)},
            selected = false,
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderPreview() {
    Reminder()
}
