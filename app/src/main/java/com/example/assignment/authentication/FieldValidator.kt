package com.example.assignment.authentication

import android.util.Patterns
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/** Shared across SignUpScreen and EditProfileScreen. */

fun validateFullName(fullName: String): String? {
    return when {
        fullName.isBlank() -> "Full name is required"
        fullName.trim().length < 2 -> "Full name is too short"
        !fullName.matches(Regex("^[a-zA-Z\\s'.-]+$")) -> "Name can only contain letters, spaces, hyphens, or apostrophes"
        else -> null
    }
}

fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Please enter a valid email address"
        else -> null
    }
}

fun validateMalaysianMobile(mobileNumber: String): String? {
    return when {
        mobileNumber.isBlank() -> "Mobile number is required"
        !mobileNumber.matches(Regex("^01[0-9]-[0-9]{7,8}$")) ->
            "Please enter a valid Malaysian mobile number (e.g. 012-3456789)"
        else -> null
    }
}

fun validateDateOfBirth(dateOfBirth: String): String? {
    if (dateOfBirth.isBlank()) return "Date of birth is required"
    if (!dateOfBirth.trim().matches(Regex("^\\d{2}/\\d{2}/\\d{4}$"))) {
        return "Date of birth must be in DD/MM/YYYY format"
    }
    return validateDateLogic(dateOfBirth.trim())
}

fun validateRelationship(relationship: String): String? {
    return when {
        relationship.isBlank() -> "Relationship is required"
        relationship.trim().length < 2 -> "Relationship is too short"
        !relationship.matches(Regex("^[a-zA-Z\\s'.-]+$")) -> "Relationship can only contain letters"
        else -> null
    }
}

/** Checks the date is real and not in the future / absurdly old. */
private fun validateDateLogic(dateOfBirth: String): String? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parsedDate = LocalDate.parse(dateOfBirth, formatter)
        when {
            parsedDate.isAfter(LocalDate.now()) -> "Date of birth cannot be in the future"
            parsedDate.isBefore(LocalDate.now().minusYears(120)) -> "Please enter a valid date of birth"
            else -> null
        }
    } catch (e: DateTimeParseException) {
        "Please enter a valid date (e.g. 31/12/2000)"
    }
}