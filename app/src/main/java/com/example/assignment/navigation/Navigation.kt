package com.example.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.authentication.LoginPage
import com.example.assignment.authentication.SignUpScreen
import com.example.assignment.homescreen.HomeScreen

@Composable
fun AppNavigation() {
    // This controller remembers where the user is in the app
    val navController = rememberNavController()

    // We start the app on the "login" screen
    NavHost(navController = navController, startDestination = "login") {

        // Route 1: The Login Screen
        composable("login") {
            LoginPage(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToHome = {
                    // When logging in, we navigate to home AND destroy the login screen
                    // so the user can't hit the phone's "back" button and go back to login!
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Route 2: The Sign Up Screen
        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Route 3: The Main Home Screen
        composable("home") {
            HomeScreen()
        }
    }
}