package com.example.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.authentication.LoginPage
import com.example.assignment.authentication.SignUpScreen
import com.example.assignment.authentication.StartingScreen
import com.example.assignment.database.AppDatabase
import com.example.assignment.homescreen.HomeScreen

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 1. Get the Context and open the Database
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()

    NavHost(navController = navController, startDestination = "starting") {

        composable("starting") {
            StartingScreen(
                onTapToContinue = {
                    navController.navigate("login") {
                        popUpTo("starting") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginPage(
                userDao = userDao,
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                userDao = userDao,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("reminders") {
            com.example.assignment.reminder.ReminderScreen(navController)
        }

        composable("appointments") {
            // Placeholder
            Text("Appointments Screen")
        }

        composable("records") {
            // Placeholder
            Text("Records Screen")
        }
    }
}

@Composable
fun BottomNavBar(navController: androidx.navigation.NavController, selectedIndex: Int) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("home", "⌂", "Home"),
            Triple("appointments", "🕒", "Appointments"),
            Triple("reminders", "⏰", "Reminders"),
            Triple("records", "☰", "Records")
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    if (selectedIndex != index) {
                        navController.navigate(item.first) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Text(
                        text = item.second,
                        fontSize = if (index == 0) 24.sp else 20.sp,
                        color = if (selectedIndex == index) Color(0xFF1E50FF) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.third,
                        fontSize = 10.sp,
                        color = if (selectedIndex == index) Color(0xFF1E50FF) else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}