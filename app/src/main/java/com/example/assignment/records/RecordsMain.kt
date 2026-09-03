package com.example.assignment.records

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment.database.RecordCategory
import com.example.assignment.viewmodel.RecordViewModel

@Composable
fun RecordsMain(
    rootNavController: NavController,
    username: String,
    viewModel: RecordViewModel,
    onBackToHome: () -> Unit
) {
    LaunchedEffect(username) {
        viewModel.setUsername(username)
    }

    val context = LocalContext.current
    val fileStorageHelper = remember { FileStorageHelper(context) }

    val recordsNavController = rememberNavController()

    NavHost(navController = recordsNavController, startDestination = "records_menu") {

        composable("records_menu") {
            RecordsMenuScreen(
                navController = recordsNavController,
                rootNavController = rootNavController,
                viewModel = viewModel,
                onBackToHome = onBackToHome,
                onUploadClick = { recordsNavController.navigate("upload_record") },
                onCategoryClick = { category -> recordsNavController.navigate("records_category/${category.name}") },
                onRecordClick = { recordId -> recordsNavController.navigate("record_detail/$recordId") }
            )
        }

        composable(
            route = "upload_record?categoryName={categoryName}",
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            val preselectedCategory = categoryName?.let { RecordCategory.valueOf(it) }
            UploadRecordScreen(
                viewModel = viewModel,
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
                onDeleteComplete = { recordsNavController.popBackStack("records_menu", inclusive = false) }
            )
        }

        composable(
            route = "records_category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: RecordCategory.LAB_RESULTS.name
            val category = RecordCategory.valueOf(categoryName)
            CategoryListScreen(
                category = category,
                viewModel = viewModel,
                onBackClick = { recordsNavController.popBackStack() },
                onUploadClick = { recordsNavController.navigate("upload_record?categoryName=${category.name}") },
                onRecordClick = { recordId -> recordsNavController.navigate("record_detail/$recordId") }
            )
        }
    }
}