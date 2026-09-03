package com.example.assignment.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ProfilePicturePicker(
    currentImageUri: String?,
    initials: String,
    showDialog: Boolean,
    onDialogDismiss: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    size: androidx.compose.ui.unit.Dp = 90.dp
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (currentImageUri != null) {
            AsyncImage(
                model = currentImageUri,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(size)
                    .background(Color(0xFFE5EDFF), shape = androidx.compose.foundation.shape.CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFE5EDFF),
                modifier = Modifier.size(size)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = initials, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E50FF))
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            title = { Text("Update Profile Picture", fontWeight = FontWeight.Bold, color = Color(0xFF1E50FF)) },
            text = { Text("Choose a photo from your gallery.") },
            confirmButton = {
                TextButton(onClick = {
                    onDialogDismiss()
                    galleryLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
                    Text("Choose from Gallery", color = Color(0xFF1E50FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDialogDismiss) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}