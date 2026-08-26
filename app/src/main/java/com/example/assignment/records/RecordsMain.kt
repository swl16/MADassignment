package com.example.assignment.records

import android.R.attr.type
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.RecordCategory
import com.example.assignment.navigation.BottomNavBar

@Composable
fun RecordsMain(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val recordDao = remember { db.recordDao() }
    val fileStorageHelper = remember { FileStorageHelper(context) }

    val recordsNavController = rememberNavController()

    NavHost(navController = recordsNavController, startDestination = "records_menu") {

        composable("records_menu") {
            RecordsMenuScreen(
                recordDao = recordDao,
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
            val preselectedCategory = categoryName?.let { RecordCategory.valueOf(it) }
            UploadRecordScreen(
                recordDao = recordDao,
                fileStorageHelper = fileStorageHelper,
                preselectedCategory = preselectedCategory,
                onBackClick = { recordsNavController.popBackStack() },
                onUploadComplete = { recordsNavController.popBackStack() }
            )
        }

        composable(
            route = "record_detail/{recordId}",
            arguments = listOf(navArgument("recordId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L
            RecordDetailScreen(
                recordId = recordId,
                recordDao = recordDao,
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
                recordDao = recordDao,
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
@Preview(showBackground = true)
@Composable
fun RecordsPreview(){
    RecordsMain(navController = rememberNavController())
}