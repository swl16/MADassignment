package com.example.assignment.ui.theme

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun appTextFieldColors(containerColor: Color = Color(0xFFE5EDFF)): TextFieldColors =
    TextFieldDefaults.colors(
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = Color(0xFF14213D),
        unfocusedTextColor = Color(0xFF14213D),
        cursorColor = Color(0xFF1E50FF)
    )