// com/example/assignment/authentication/PhoneFormatting.kt
package com.example.assignment.authentication

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Formats raw digit input into Malaysian mobile format and keeps the
 * cursor positioned correctly after the newly-typed digit (not at the end).
 * Most prefixes: 01X-XXXXXXX (10 digits total, e.g. 012-3456789)
 * 011 prefix:    011-XXXXXXXX (11 digits total, e.g. 011-12345678)
 *
 * Shared by SignUpScreen, EditProfileScreen, and EmergencyContactScreen.
 */
fun formatMalaysianMobile(input: TextFieldValue): TextFieldValue {
    val originalCursor = input.selection.start
    val rawDigitsBeforeCursor = input.text.take(originalCursor).count { it.isDigit() }

    val digits = input.text.filter { it.isDigit() }
    val maxLength = if (digits.startsWith("011")) 11 else 10
    val capped = digits.take(maxLength)

    val formatted = if (capped.length <= 3) {
        capped
    } else {
        "${capped.substring(0, 3)}-${capped.substring(3)}"
    }

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

    return TextFieldValue(
        text = formatted,
        selection = TextRange(newCursor)
    )
}