package com.example.assignment.records

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.Record
import com.example.assignment.database.RecordCategory
import com.example.assignment.database.RecordDao
import kotlinx.coroutines.launch

/**
 * Shared list screen for all 4 categories: Lab Results, Prescriptions, Vaccination, Imaging.
 * Reused by RecordsMain for all 4 "records_category/{categoryName}" destinations —
 * matches your designs, which are identical in layout aside from title, search hint, and filtered data.
 */
@Composable
fun CategoryListScreen(
    category: RecordCategory,
    recordDao: RecordDao,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    onRecordClick: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember(category) { mutableStateOf("") }
    var recordPendingDelete by remember { mutableStateOf<Record?>(null) }

    val records by remember(category, searchQuery) {
        if (searchQuery.isBlank()) recordDao.getRecordsByCategory(category)
        else recordDao.searchRecordsInCategory(category, searchQuery)
    }.collectAsState(initial = emptyList())

    val screenTitle = when (category) {
        RecordCategory.LAB_RESULTS -> "Lab Results"
        RecordCategory.PRESCRIPTIONS -> "Prescriptions"
        RecordCategory.VACCINATION -> "Vaccination"
        RecordCategory.IMAGING -> "Imaging"
    }
    val searchHint = "Search ${category.displayName.lowercase()}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecordsScreenBg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // Top bar: back + title + Upload button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(text = screenTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
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

        Spacer(Modifier.height(8.dp))

        Text(text = "${records.size} files", fontSize = 14.sp, color = TextSecondary)

        Spacer(Modifier.height(12.dp))

        RecordsSearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = searchHint
        )

        Spacer(Modifier.height(20.dp))

        Text(text = "All files", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(records, key = { it.id }) { record ->
                CategoryListItem(
                    record = record,
                    onClick = { onRecordClick(record.id) },
                    onDeleteClick = { recordPendingDelete = record }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // "Files are encrypted and stored securely." footer banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BadgeBlueBg, RoundedCornerShape(14.dp))
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Files are encrypted and stored securely.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryBlue
            )
        }

        Spacer(Modifier.height(20.dp))
    }

    // Delete confirmation, triggered from a list item's ⋮ menu
    recordPendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { recordPendingDelete = null },
            title = { Text("Delete this record?") },
            text = { Text("\"${record.title}\" will be permanently deleted. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        recordDao.delete(record)
                        recordPendingDelete = null
                    }
                }) {
                    Text("Delete", color = androidx.compose.ui.graphics.Color(0xFFE0483E))
                }
            },
            dismissButton = {
                TextButton(onClick = { recordPendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CategoryListItem(
    record: Record,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecordCategoryBadge(code = record.category.code)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = record.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = buildString {
                        append(formatRecordDate(record.recordDateMillis))
                        record.provider?.let { append(" • $it") }
                    },
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = TextSecondary)
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("View details") },
                        onClick = {
                            menuExpanded = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = androidx.compose.ui.graphics.Color(0xFFE0483E)) },
                        onClick = {
                            menuExpanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}