package com.example.assignment.records

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.assignment.database.Record
import com.example.assignment.database.RecordCategory
import com.example.assignment.database.RecordDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(
    recordId: Long,
    recordDao: RecordDao,
    fileStorageHelper: FileStorageHelper,
    onBackClick: () -> Unit,
    onDeleteComplete: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // The record as loaded from Room (source of truth, unedited)
    var originalRecord by remember { mutableStateOf<Record?>(null) }

    // Local editable copies — only written back to Room when "Save Changes" is tapped
    var editedTitle by remember { mutableStateOf("") }
    var editedCategory by remember { mutableStateOf(RecordCategory.LAB_RESULTS) }
    var editedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(recordId) {
        val record = recordDao.getRecordById(recordId)
        originalRecord = record
        record?.let {
            editedTitle = it.title
            editedCategory = it.category
            editedDateMillis = it.recordDateMillis
        }
    }

    val record = originalRecord

    if (record == null) {
        // Still loading, or record was deleted from elsewhere
        Box(
            modifier = Modifier.fillMaxSize().background(RecordsScreenBg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    val hasChanges = editedTitle != record.title ||
            editedCategory != record.category ||
            editedDateMillis != record.recordDateMillis

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecordsScreenBg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(text = "Medical Record", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(Modifier.height(20.dp))

        // File preview panel
        FilePreviewPanel(
            record = record,
            fileStorageHelper = fileStorageHelper
        )

        Spacer(Modifier.height(24.dp))

        Text(text = "Record information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(Modifier.height(12.dp))

        FieldLabel("Record title")
        OutlinedTextField(
            value = editedTitle,
            onValueChange = { editedTitle = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = CardWhite,
                focusedContainerColor = CardWhite,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryBlue
            )
        )

        Spacer(Modifier.height(16.dp))

        FieldLabel("Category")
        CategoryDropdown(
            selected = editedCategory,
            onSelected = { editedCategory = it }
        )

        Spacer(Modifier.height(16.dp))

        FieldLabel("Record date")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardWhite)
                .clickable { showDatePicker = true }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(text = formatRecordDate(editedDateMillis), fontSize = 15.sp, color = TextPrimary)
        }

        Spacer(Modifier.height(28.dp))

        // Save Changes button
        Button(
            onClick = {
                isSaving = true
                scope.launch {
                    val updated = record.copy(
                        title = editedTitle.trim(),
                        category = editedCategory,
                        recordDateMillis = editedDateMillis
                    )
                    recordDao.update(updated)
                    originalRecord = updated
                    isSaving = false
                }
            },
            enabled = editedTitle.isNotBlank() && hasChanges && !isSaving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(26.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Delete Record button — outlined red, matches design
        OutlinedButton(
            onClick = { showDeleteConfirm = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            border = BorderStrokeRed,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE0483E)),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Delete Record", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(20.dp))
    }

    if (showDatePicker) {
        RecordDatePickerDialog(
            initialMillis = editedDateMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                editedDateMillis = millis
                showDatePicker = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this record?") },
            text = { Text("\"${record.title}\" will be permanently deleted. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        fileStorageHelper.deleteFile(record.fileName)
                        recordDao.delete(record)
                        showDeleteConfirm = false
                        onDeleteComplete()
                    }
                }) {
                    Text("Delete", color = Color(0xFFE0483E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun FilePreviewPanel(
    record: Record,
    fileStorageHelper: FileStorageHelper
) {
    val isImage = record.fileType == "JPG" || record.fileType == "PNG" || record.fileType == "JPEG"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BadgeBlueBg),
        contentAlignment = Alignment.Center
    ) {
        if (isImage) {
            val file = remember(record.fileName) { fileStorageHelper.getFile(record.fileName) }
            AsyncImage(
                model = file,
                contentDescription = record.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = record.fileType,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = record.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Uploaded ${formatRecordDate(record.uploadedAtMillis)}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

private val BorderStrokeRed = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0483E))