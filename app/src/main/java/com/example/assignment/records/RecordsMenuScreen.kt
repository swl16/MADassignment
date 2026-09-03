package com.example.assignment.records

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.R
import com.example.assignment.database.Record
import com.example.assignment.database.RecordCategory
import com.example.assignment.navigation.BottomNavBar
import com.example.assignment.viewmodel.RecordViewModel

val PrimaryBlue = Color(0xFF2F6BFF)
val BadgeBlueBg = Color(0xFFE3ECFF)
val RecordsScreenBg = Color(0xFFEFF3FA)
val CardWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF14213D)
val TextSecondary = Color(0xFF8891A5)
val ErrorRed = Color(0xFFE53935)

@Composable
fun RecordsMenuScreen(
    navController: androidx.navigation.NavController,
    rootNavController: androidx.navigation.NavController,
    viewModel: RecordViewModel,
    onBackToHome: () -> Unit,
    onUploadClick: () -> Unit,
    onCategoryClick: (RecordCategory) -> Unit,
    onRecordClick: (String) -> Unit
) {
    // SAFE BACK LOGIC: If navController can pop a nested screen (like detail/category),
    // it pops it. If it is on the root records menu, it safely delegates to onBackToHome.
    val handleBackNavigation = {
        if (!navController.popBackStack()) {
            onBackToHome()
        }
    }

    val allRecords by viewModel.records.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchRecords()
    }

    val recentRecords = remember(allRecords, searchQuery) {
        val filtered = if (searchQuery.isBlank()) allRecords
        else allRecords.filter { it.title.contains(searchQuery, ignoreCase = true) }
        filtered.sortedByDescending { it.uploadedAt }.take(if (searchQuery.isBlank()) 3 else filtered.size)
    }

    val categoryCounts = remember(allRecords) {
        RecordCategory.entries.associateWith { category -> allRecords.count { it.category == category } }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = rootNavController, selectedIndex = 3)
        },
        containerColor = RecordsScreenBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { handleBackNavigation() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = "Back",
                        tint = TextSecondary
                    )
                }
                Text(
                    text = "Medical Records",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Button(
                    onClick = onUploadClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Upload")
                }
            }

            Spacer(Modifier.height(16.dp))
            RecordsSearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search records"
            )
            Spacer(Modifier.height(24.dp))
            Text(text = "Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RecordCategoryCard(
                        Modifier.weight(1f),
                        RecordCategory.LAB_RESULTS.displayName,
                        categoryCounts[RecordCategory.LAB_RESULTS] ?: 0
                    ) { onCategoryClick(RecordCategory.LAB_RESULTS) }
                    RecordCategoryCard(
                        Modifier.weight(1f),
                        RecordCategory.PRESCRIPTIONS.displayName,
                        categoryCounts[RecordCategory.PRESCRIPTIONS] ?: 0
                    ) { onCategoryClick(RecordCategory.PRESCRIPTIONS) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RecordCategoryCard(
                        Modifier.weight(1f),
                        RecordCategory.VACCINATION.displayName,
                        categoryCounts[RecordCategory.VACCINATION] ?: 0
                    ) { onCategoryClick(RecordCategory.VACCINATION) }
                    RecordCategoryCard(
                        Modifier.weight(1f),
                        RecordCategory.IMAGING.displayName,
                        categoryCounts[RecordCategory.IMAGING] ?: 0
                    ) { onCategoryClick(RecordCategory.IMAGING) }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(text = "Recent files", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(12.dp))

            if (recentRecords.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isBlank()) "No records yet. Tap Upload to add one." else "No records match \"$searchQuery\".",
                        color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(recentRecords, key = { it.id }) { record ->
                        RecordListItem(record = record, onClick = { onRecordClick(record.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun RecordsSearchField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextSecondary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = CardWhite, focusedContainerColor = CardWhite,
            unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue
        )
    )
}

@Composable
fun RecordCategoryCard(modifier: Modifier = Modifier, title: String, fileCount: Int, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(text = "$fileCount files", fontSize = 13.sp, color = TextSecondary)
        }
    }
}

@Composable
fun RecordListItem(record: Record, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            RecordCategoryBadge(code = record.category.code)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = record.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(text = "${formatRecordDate(record.recordDate)} • ${record.fileType}", fontSize = 13.sp, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}

@Composable
fun RecordCategoryBadge(code: String) {
    Box(modifier = Modifier.size(44.dp).background(BadgeBlueBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
        Text(text = code, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
    }
}

fun formatRecordDate(isoString: String): String {
    return try {
        val instant = java.time.Instant.parse(isoString)
        java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date.from(instant))
    } catch (e: Exception) {
        isoString
    }
}