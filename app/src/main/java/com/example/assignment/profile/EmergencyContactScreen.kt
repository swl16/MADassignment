package com.example.assignment.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.database.EmergencyContact
import com.example.assignment.ui.theme.appTextFieldColors
import com.example.assignment.viewmodel.EmergencyContactViewModel

@Composable
fun EmergencyContactScreen(
    viewModel: EmergencyContactViewModel, // CHANGED: Injected ViewModel
    username: String,
    onNavigateBack: () -> Unit
) {
    val existingContact by viewModel.contact.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    // Load data via ViewModel
    LaunchedEffect(username) {
        viewModel.loadContact(username)
    }

    // Populate form fields when data loads/changes
    LaunchedEffect(existingContact) {
        existingContact?.let {
            fullName = it.fullName
            relationship = it.relationship
            mobile = it.mobileNumber
        }
    }

    val initials = if (fullName.isNotBlank()) fullName.take(2).uppercase() else "--"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        StandardTopBar(
            title = "Emergency Contact",
            actionText = "",
            onBackClick = onNavigateBack,
            onActionClick = {}
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "This person can be contacted during an emergency.",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (existingContact != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = Color(0xFFE5EDFF),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initials,
                                    color = Color(0xFF1E50FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$relationship • Primary contact",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mobile,
                                fontSize = 14.sp,
                                color = Color(0xFF1E50FF),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                Text(
                    text = "No emergency contact saved yet — add one below.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "Edit contact",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(16.dp))

            ContactInputField(label = "Full name", value = fullName) { fullName = it }
            ContactInputField(label = "Relationship", value = relationship) { relationship = it }
            ContactInputField(label = "Mobile number", value = mobile) { mobile = it }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    val contactToSave = existingContact?.copy(
                        fullName = fullName,
                        relationship = relationship,
                        mobileNumber = mobile
                    ) ?: EmergencyContact(
                        username = username,
                        fullName = fullName,
                        relationship = relationship,
                        mobileNumber = mobile
                    )

                    viewModel.saveContact(contactToSave, isExisting = existingContact != null) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
            ) {
                Text(text = "Save Contact", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delete Button
            OutlinedButton(
                onClick = {
                    existingContact?.let { contact ->
                        viewModel.deleteContact(contact) {
                            onNavigateBack()
                        }
                    }
                },
                enabled = existingContact != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
            ) {
                Text(text = "Delete Contact", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ContactInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = appTextFieldColors(Color.White),
            shape = RoundedCornerShape(12.dp)
        )
    }
}