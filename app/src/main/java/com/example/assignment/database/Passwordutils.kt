package com.example.assignment.database

import java.security.MessageDigest

/**
 * One-way SHA-256 hash for storing/checking passwords.
 * Used by SignUpScreen (before insertUser), LoginPage (before login query),
 * and SetPasswordScreen (before updateUser) — never store or compare raw password text.
 */
fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}