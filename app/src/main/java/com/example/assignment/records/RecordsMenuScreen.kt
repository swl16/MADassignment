package com.example.assignment.records

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.Record
import com.example.assignment.database.RecordCategory
import com.example.assignment.database.RecordDao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val PrimaryBlue = Color(0xFF2F6BFF)
val BadgeBlueBg = Color(0xFFE3ECFF)
val RecordsScreenBg = Color(0xFFEFF3FA)
val CardWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF14213D)
val TextSecondary = Color(0xFF8891A5)

@Composable
fun RecordsMenuScreen(
    recordDao: RecordDao,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    onCategoryClick: (RecordCategory) -> Unit,
    onRecordClick: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResults by remember(searchQuery) {
        if (searchQuery.isBlank()) recordDao.getRecentRecords(3)
        else recordDao.searchRecords(searchQuery)
    }.collectAsState(initial = emptyList())

    val labCount by recordDao.getCategoryCount(RecordCategory.LAB_RESULTS).collectAsState(initial = 0)
    val rxCount by recordDao.getCategoryCount(RecordCategory.PRESCRIPTIONS).collectAsState(initial = 0)
    val vacCount by recordDao.getCategoryCount(RecordCategory.VACCINATION).collectAsState(initial = 0)
    val imgCount by recordDao.getCategoryCount(RecordCategory.IMAGING).collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecordsScreenBg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(text = "Medical Records", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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
                    modifier = Modifier.weight(1f),
                    title = RecordCategory.LAB_RESULTS.displayName,
                    fileCount = labCount,
                    onClick = { onCategoryClick(RecordCategory.LAB_RESULTS) }
                )
                RecordCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = RecordCategory.PRESCRIPTIONS.displayName,
                    fileCount = rxCount,
                    onClick = { onCategoryClick(RecordCategory.PRESCRIPTIONS) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RecordCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = RecordCategory.VACCINATION.displayName,
                    fileCount = vacCount,
                    onClick = { onCategoryClick(RecordCategory.VACCINATION) }
                )
                RecordCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = RecordCategory.IMAGING.displayName,
                    fileCount = imgCount,
                    onClick = { onCategoryClick(RecordCategory.IMAGING) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(text = "Recent files", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(searchResults, key = { it.id }) { record ->
                RecordListItem(record = record, onClick = { onRecordClick(record.id) })
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
            unfocusedContainerColor = CardWhite,
            focusedContainerColor = CardWhite,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = PrimaryBlue
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
                    text = "${formatRecordDate(record.recordDateMillis)} • ${record.fileType}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}

@Composable
fun RecordCategoryBadge(code: String) {
    Box(
        modifier = Modifier.size(44.dp).background(BadgeBlueBg, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = code, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
    }
}

fun formatRecordDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
