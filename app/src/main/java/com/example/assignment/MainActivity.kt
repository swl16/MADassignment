package com.example.assignment // Keep your actual package name here!

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.authentication.LoginPage
import com.example.assignment.authentication.SignUpScreen
import com.example.assignment.authentication.StartingScreen // Added the import for your new screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Your app theme wrapper might be here, keep it if it is!
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 1. We change the startDestination to "starting"
    NavHost(navController = navController, startDestination = "starting") {

        // 2. THE STARTING SCREEN ROUTE
        composable("starting") {
            StartingScreen(
                onTapToContinue = {
                    // Navigate to 'login'
                    navController.navigate("login") {
                        // This removes 'starting' from the back history
                        popUpTo("starting") { inclusive = true }
                    }
                }
            )
        }

        // 3. THE LOGIN ROUTE
        composable("login") {
            LoginPage(
                onNavigateToSignUp = {
                    navController.navigate("signup")
                }
            )
        }

        // 4. THE SIGN UP ROUTE
        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}