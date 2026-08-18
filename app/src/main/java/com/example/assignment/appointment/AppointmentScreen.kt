package com.example.assignment.appointment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignment.navigation.BottomNavBar

@Composable
fun  AppointmentMain(navController: NavController){
    Scaffold(
        containerColor = Color(0xFFF8FAFF),
        bottomBar = {
            BottomNavBar(navController = navController, selectedIndex = 1)
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(16.dp)
        ){
            Text(
                text = "Book Appointment",
                fontSize = 25.sp,
                fontWeight = SemiBold
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun appointmentPreview(){
    AppointmentMain(navController = rememberNavController() )
}
