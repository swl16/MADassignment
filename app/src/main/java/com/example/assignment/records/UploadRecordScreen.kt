package com.example.assignment.records

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.RecordCategory
import com.example.assignment.viewmodel.RecordViewModel

private val ALLOWED_EXTENSIONS = setOf("pdf", "jpg", "jpeg", "png")
private const val MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024 // 10 MB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadRecordScreen(
    viewModel: RecordViewModel,
    preselectedCategory: RecordCategory? = null,
    onBackClick: () -> Unit,
    onUploadComplete: () -> Unit
) {
    var selectedFileUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var selectedFileDisplayName by remember { mutableStateOf<String?>(null) }
    var recordTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(preselectedCategory ?: RecordCategory.LAB_RESULTS) }
    var recordDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var providerName by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Requirement #2: errors only appear AFTER the user presses Upload, not while typing —
    // otherwise every field shows red before they've had a chance to fill it in.
    var fileError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileDisplayName = viewModel.getFileName(uri)
            fileError = null // clear any previous "please choose a file" error once they pick one
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(RecordsScreenBg).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(text = "Upload Record", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(Modifier.height(20.dp))

        // Upload dropzone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(BadgeBlueBg, RoundedCornerShape(16.dp))
                .clickable { filePickerLauncher.launch(arrayOf("application/pdf", "image/jpeg", "image/png")) },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.UploadFile, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(text = selectedFileDisplayName ?: "Choose a file to upload", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(text = "PDF, JPG or PNG • Maximum 10 MB", fontSize = 12.sp, color = TextSecondary)
            }
        }
        // Requirement #2 + #3: shows either "please choose a file" or a specific
        // type/size rejection message, right under the dropzone.
        if (fileError != null) {
            Text(text = fileError!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
        }

        Spacer(Modifier.height(24.dp))

        FieldLabel("Record title")
        OutlinedTextField(
            value = recordTitle,
            onValueChange = { recordTitle = it; if (it.isNotBlank()) titleError = null },
            placeholder = { Text("e.g. Blood Test Report", color = TextSecondary) },
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
        CategoryDropdown(selected = selectedCategory, onSelected = { selectedCategory = it })

        Spacer(Modifier.height(16.dp))
        FieldLabel("Doctor / Clinic (optional)")
        OutlinedTextField(
            value = providerName,
            onValueChange = { providerName = it },
            placeholder = { Text("e.g. Dr. Sarah Lim", color = TextSecondary) },
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
            Text(text = formatRecordDateMillis(recordDateMillis), fontSize = 15.sp, color = TextPrimary)
        }

        Spacer(Modifier.height(28.dp))

        // Requirement #2: button is ALWAYS pressable (not disabled by isFormValid anymore).
        // Pressing it with missing/invalid input shows inline errors instead of doing nothing.
        Button(
            onClick = {
                val uri = selectedFileUri
                val trimmedTitle = recordTitle.trim()

                // --- Requirement #2: required-field checks ---
                fileError = if (uri == null) "Please choose a file to upload." else null
                titleError = if (trimmedTitle.isBlank()) "Please enter a record title." else null

                if (uri == null || trimmedTitle.isBlank()) return@Button // stop here, errors are now visible

                // --- Requirement #3: file type check (defense in depth beyond the picker's MIME filter) ---
                val displayName = selectedFileDisplayName ?: ""
                val extension = displayName.substringAfterLast('.', "").lowercase()
                if (extension !in ALLOWED_EXTENSIONS) {
                    fileError = "Unsupported file type. Please upload a PDF, JPG, or PNG file."
                    return@Button
                }

                // --- Requirement #3: file size check ---
                val fileSize = viewModel.getFileSize(uri)
                if (fileSize != null && fileSize > MAX_FILE_SIZE_BYTES) {
                    fileError = "File is too large (${fileSize / (1024 * 1024)} MB). Maximum size is 10 MB."
                    return@Button
                }

                isUploading = true
                viewModel.uploadRecord(uri, trimmedTitle, selectedCategory, recordDateMillis, providerName.trim())
                isUploading = false
                onUploadComplete()
            },
            enabled = !isUploading, // only disabled WHILE uploading — never disabled due to empty fields
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(26.dp)
        ) {
            if (isUploading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            else Text("Upload record", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(20.dp))
    }

    if (showDatePicker) {
        RecordDatePickerDialog(
            initialMillis = recordDateMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis -> recordDateMillis = millis; showDatePicker = false }
        )
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.padding(bottom = 6.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(selected: RecordCategory, onSelected: (RecordCategory) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.displayName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary) },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = CardWhite, focusedContainerColor = CardWhite,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RecordCategory.entries.forEach { category ->
                DropdownMenuItem(text = { Text(category.displayName) }, onClick = { onSelected(category); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDatePickerDialog(initialMillis: Long, onDismiss: () -> Unit, onConfirm: (Long) -> Unit) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { onConfirm(it) } ?: onDismiss() }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) { DatePicker(state = datePickerState) }
}

fun formatRecordDateMillis(millis: Long): String {
    val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(millis))
}