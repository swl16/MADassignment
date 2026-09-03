package com.example.assignment.records

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.assignment.database.Record
import com.example.assignment.database.RecordCategory
import com.example.assignment.viewmodel.RecordViewModel
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(
    recordId: String,
    viewModel: RecordViewModel,
    fileStorageHelper: FileStorageHelper,
    onBackClick: () -> Unit,
    onDeleteComplete: () -> Unit
) {
    val record by viewModel.selectedRecord.collectAsState()

    var editedTitle by remember { mutableStateOf("") }
    var editedCategory by remember { mutableStateOf(RecordCategory.LAB_RESULTS) }
    var editedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var editedProvider by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    LaunchedEffect(record) {
        record?.let {
            editedTitle = it.title
            editedCategory = it.category
            editedDateMillis = try { Instant.parse(it.recordDate).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() }
            editedProvider = it.provider ?: ""
        }
    }

    val currentRecord = record

    if (currentRecord == null) {
        Box(modifier = Modifier.fillMaxSize().background(RecordsScreenBg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    val hasChanges = editedTitle != currentRecord.title ||
            editedCategory != currentRecord.category ||
            (editedProvider != (currentRecord.provider ?: "")) ||
            run {
                val originalMillis = try { Instant.parse(currentRecord.recordDate).toEpochMilli() } catch (e: Exception) { editedDateMillis }
                originalMillis != editedDateMillis
            }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecordsScreenBg)
            .verticalScroll(rememberScrollState()) // Enables scrolling
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(text = "Medical Record", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(Modifier.height(20.dp))
        FilePreviewPanel(record = currentRecord, viewModel = viewModel)
        Spacer(Modifier.height(24.dp))
        Text(text = "Record information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(12.dp))

        FieldLabel("Record title")
        OutlinedTextField(
            value = editedTitle,
            onValueChange = { editedTitle = it; if (it.isNotBlank()) titleError = null },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = titleError != null,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = CardWhite, focusedContainerColor = CardWhite,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue
            )
        )
        if (titleError != null) {
            Text(text = titleError!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(Modifier.height(16.dp))
        FieldLabel("Category")
        CategoryDropdown(selected = editedCategory, enabled = true, onSelected = { editedCategory = it })

        Spacer(Modifier.height(16.dp))
        FieldLabel("Doctor / Clinic (optional)")
        OutlinedTextField(
            value = editedProvider,
            onValueChange = { editedProvider = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = CardWhite, focusedContainerColor = CardWhite,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue
            )
        )

        Spacer(Modifier.height(16.dp))
        FieldLabel("Record date")
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardWhite)
                .clickable { showDatePicker = true }.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(text = formatRecordDateMillis(editedDateMillis), fontSize = 15.sp, color = TextPrimary)
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                val trimmedTitle = editedTitle.trim()
                titleError = if (trimmedTitle.isBlank()) "Please enter a record title." else null
                if (trimmedTitle.isBlank()) return@Button

                val updatedRecord = currentRecord.copy(
                    title = trimmedTitle,
                    categoryName = editedCategory.name,
                    recordDate = Instant.ofEpochMilli(editedDateMillis).toString(),
                    recordDateMillis = editedDateMillis,
                    provider = editedProvider.trim().ifBlank { null }
                )
                viewModel.updateRecord(updatedRecord)
            },
            enabled = hasChanges,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { showDeleteConfirm = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            border = BorderStroke(1.dp, ErrorRed),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Delete Record", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(32.dp))
    }

    if (showDatePicker) {
        RecordDatePickerDialog(
            initialMillis = editedDateMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis -> editedDateMillis = millis; showDatePicker = false }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this record?") },
            text = { Text("\"${currentRecord.title}\" will be permanently deleted. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deleteRecord(currentRecord) {
                        onDeleteComplete()
                    }
                }) { Text("Delete", color = ErrorRed) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun FilePreviewPanel(record: Record, viewModel: RecordViewModel) {
    val isImage = record.fileType in listOf("JPG", "JPEG", "PNG")
    var signedUrl by remember(record.filePath) { mutableStateOf<String?>(null) }
    var urlLoadFailed by remember(record.filePath) { mutableStateOf(false) }

    LaunchedEffect(record.filePath, isImage) {
        if (isImage) {
            val url = viewModel.getSignedUrl(record.filePath)
            if (url != null) signedUrl = url else urlLoadFailed = true
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).background(BadgeBlueBg), contentAlignment = Alignment.Center) {
        when {
            isImage && signedUrl != null -> AsyncImage(
                model = signedUrl,
                contentDescription = record.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            isImage && urlLoadFailed -> Text(
                text = "Preview unavailable offline",
                color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            isImage -> CircularProgressIndicator(color = PrimaryBlue)
            else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = record.fileType, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                Spacer(Modifier.height(8.dp))
                Text(text = record.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(text = "Uploaded ${formatRecordDate(record.uploadedAt)}", fontSize = 13.sp, color = TextSecondary)
            }
        }
    }
}