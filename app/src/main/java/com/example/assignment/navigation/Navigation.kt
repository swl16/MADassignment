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
            HomeScreen()
        }
    }
}