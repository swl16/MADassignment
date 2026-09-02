package com.example.assignment.records

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment.database.RecordCategory
import com.example.assignment.navigation.BottomNavBar
import com.example.assignment.viewmodel.RecordViewModel

@Composable
fun RecordsMain(
    navController: NavController,
    username: String,
    viewModel: RecordViewModel
) {
    val context = LocalContext.current
    val fileStorageHelper = remember { FileStorageHelper(context) }
    val recordsNavController = rememberNavController()

    // Sync records from remote when entering the records section
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            viewModel.syncRecords(username)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, selectedIndex = 3)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = recordsNavController,
                startDestination = "records_menu",
                modifier = Modifier.fillMaxSize()
            ) {

                composable("records_menu") {
                    RecordsMenuScreen(
                        viewModel = viewModel,
                        username = username,
                        onBackClick = { navController.popBackStack() },
                        onUploadClick = { recordsNavController.navigate("upload_record") },
                        onCategoryClick = { category ->
                            recordsNavController.navigate("records_category/${category.name}")
                        },
                        onRecordClick = { recordId ->
                            recordsNavController.navigate("record_detail/$recordId")
                        }
                    )
                }

                composable(
                    route = "upload_record?categoryName={categoryName}",
                    arguments = listOf(
                        navArgument("categoryName") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val categoryName = backStackEntry.arguments?.getString("categoryName")
                    val preselectedCategory = categoryName?.let { 
                        try { RecordCategory.valueOf(it) } catch(e: Exception) { null } 
                    }
                    UploadRecordScreen(
                        viewModel = viewModel,
                        username = username,
                        fileStorageHelper = fileStorageHelper,
                        preselectedCategory = preselectedCategory,
                        onBackClick = { recordsNavController.popBackStack() },
                        onUploadComplete = { recordsNavController.popBackStack() }
                    )
                }

                composable(
                    route = "record_detail/{recordId}",
                    arguments = listOf(navArgument("recordId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val recordId = backStackEntry.arguments?.getString("recordId") ?: ""
                    RecordDetailScreen(
                        recordId = recordId,
                        viewModel = viewModel,
                        fileStorageHelper = fileStorageHelper,
                        onBackClick = { recordsNavController.popBackStack() },
                        onDeleteComplete = {
                            recordsNavController.popBackStack("records_menu", inclusive = false)
                        }
                    )
                }

                composable(
                    route = "records_category/{categoryName}",
                    arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
                ) { backStackEntry ->
                    val categoryName = backStackEntry.arguments?.getString("categoryName")
                        ?: RecordCategory.LAB_RESULTS.name
                    val category = RecordCategory.valueOf(categoryName)
                    CategoryListScreen(
                        category = category,
                        viewModel = viewModel,
                        username = username,
                        onBackClick = { recordsNavController.popBackStack() },
                        onUploadClick = {
                            recordsNavController.navigate("upload_record?categoryName=${category.name}")
                        },
                        onRecordClick = { recordId ->
                            recordsNavController.navigate("record_detail/$recordId")
                        }
                    )
                }
            }
        }
    }
}
