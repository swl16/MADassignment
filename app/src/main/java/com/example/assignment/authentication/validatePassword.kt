package com.example.assignment.authentication


fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        password.length > 32 -> "Password must be less than 32 characters"
        password.contains(" ") -> "Password cannot contain spaces"
        !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
        !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
        !password.any { it.isDigit() } -> "Password must contain at least one number"
        !password.any { !it.isLetterOrDigit() } -> "Password must contain at least one special character"
        else -> null
    }
}