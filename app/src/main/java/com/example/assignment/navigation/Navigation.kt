package com.example.assignment.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.authentication.LoginPage
import com.example.assignment.authentication.SignUpScreen
import com.example.assignment.authentication.StartingScreen
import com.example.assignment.database.AppDatabase
import com.example.assignment.homescreen.HomeScreen
import com.example.assignment.profile.EditProfileScreen
import com.example.assignment.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 1. Get the Context and open the Database
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()

    NavHost(navController = navController, startDestination = "starting") {

        composable("starting") {
            StartingScreen {
                navController.navigate("login") {
                    popUpTo("starting") { inclusive = true }
                }
            }
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
            HomeScreen(
                navController = navController,
                userDao = userDao,
                onNavigateToProfile = { navController.navigate("profile")},
                onNavigateToNotifications = { navController.navigate("notifications") } // <-- ADD THIS LINE
            )
        }

        composable("reminders") {
            com.example.assignment.reminder.ReminderScreen(navController)
        }

        composable("appointments") {
            // Placeholder
            com.example.assignment.appointment.AppointmentMain(navController)
        }

        composable("records") {
            com.example.assignment.records.RecordsMain(navController)
        }

        composable("profile") {
            ProfileScreen(
                userDao = userDao,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("edit_profile") },
                onNavigateToSettings = { navController.navigate("notification_settings") },
                onNavigateToEmergency = { navController.navigate("emergency_contact") },
                onLogOut = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                userDao = userDao,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("notifications") {
            com.example.assignment.notification.NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("notification_settings") {
            com.example.assignment.profile.NotificationSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("emergency_contact") {
            com.example.assignment.profile.EmergencyContactScreen(
                onNavigateBack = { navController.popBackStack() }
            )
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