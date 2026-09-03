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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.authentication.formatMalaysianMobile
import com.example.assignment.authentication.validateFullName
import com.example.assignment.authentication.validateMalaysianMobile
import com.example.assignment.authentication.validateRelationship
import com.example.assignment.database.EmergencyContact
import com.example.assignment.ui.theme.appTextFieldColors
import com.example.assignment.viewmodel.EmergencyContactViewModel

@Composable
fun EmergencyContactScreen(
    viewModel: EmergencyContactViewModel,
    username: String,
    onNavigateBack: () -> Unit
) {
    val existingContact by viewModel.contact.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var mobileField by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(username) {
        viewModel.loadContact(username)
    }

    LaunchedEffect(existingContact) {
        existingContact?.let {
            fullName = it.fullName
            relationship = it.relationship
            mobileField = TextFieldValue(it.mobileNumber)
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
                                text = mobileField.text,
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

            //Mobile number uses Malaysian formatting
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(
                    text = "Mobile number",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = mobileField,
                    onValueChange = { input -> mobileField = formatMalaysianMobile(input) },
                    placeholder = { androidx.compose.material3.Text("012-3456789", color = Color(0xFF8DA6FF)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = appTextFieldColors(Color.White),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    )
                )
            }

            //inline error message
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    val error = validateFullName(fullName)
                        ?: validateRelationship(relationship)
                        ?: validateMalaysianMobile(mobileField.text)

                    if (error != null) {
                        errorMessage = error
                    } else {
                        errorMessage = ""
                        val contactToSave = existingContact?.copy(
                            fullName = fullName.trim(),
                            relationship = relationship.trim(),
                            mobileNumber = mobileField.text.trim()
                        ) ?: EmergencyContact(
                            username = username,
                            fullName = fullName.trim(),
                            relationship = relationship.trim(),
                            mobileNumber = mobileField.text.trim()
                        )

                        viewModel.saveContact(contactToSave, isExisting = existingContact != null) {
                            onNavigateBack()
                        }
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

/**
 * Cursor-safe Malaysian mobile formatter for TextFieldValue —
 * same logic reused from SignUpScreen / EditProfileScreen.
 */
private fun formatMalaysianMobileContact(input: TextFieldValue): TextFieldValue {
    val originalCursor = input.selection.start
    val rawDigitsBeforeCursor = input.text.take(originalCursor).count { it.isDigit() }

    val digits = input.text.filter { it.isDigit() }
    val maxLength = if (digits.startsWith("011")) 11 else 10
    val capped = digits.take(maxLength)

    val formatted = if (capped.length <= 3) capped else "${capped.substring(0, 3)}-${capped.substring(3)}"

    var digitsSeen = 0
    var newCursor = formatted.length
    for (i in formatted.indices) {
        if (formatted[i].isDigit()) {
            digitsSeen++
            if (digitsSeen == rawDigitsBeforeCursor) {
                newCursor = i + 1
                break
            }
        }
    }
    if (rawDigitsBeforeCursor == 0) newCursor = 0

    return TextFieldValue(text = formatted, selection = TextRange(newCursor))
}