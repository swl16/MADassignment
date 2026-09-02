package com.example.assignment.database

enum class RecordCategory(val code: String, val displayName: String) {
    LAB_RESULTS("LAB", "Lab results"),
    PRESCRIPTIONS("RX", "Prescriptions"),
    VACCINATION("VAC", "Vaccination"),
    IMAGING("IMG", "Imaging")
}