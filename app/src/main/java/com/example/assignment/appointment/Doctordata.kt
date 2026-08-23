package com.example.assignment.appointment

// ---------- Shared doctor data (hardcoded for now — swap for a DoctorDao later if needed) ----------

data class Doctor(
    val name: String,
    val specialty: String,
    val rating: Double,
    val availability: String,
    val experienceYears: Int,
    val patientsCount: String,   // display string, e.g. "1.2k"
    val about: String,
    val availableTimes: List<String>,
    val location: String
) {
    // used for the avatar circle, e.g. "Dr. Sarah Lim" -> "SL"
    val initials: String
        get() = name.removePrefix("Dr. ").split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
}

val sampleDoctors = listOf(
    Doctor(
        name = "Dr. Sarah Lim",
        specialty = "General Medicine",
        rating = 4.9,
        availability = "9:00 AM - 4:00 PM",
        experienceYears = 8,
        patientsCount = "1.2k",
        about = "Dr. Sarah provides primary care, health screening, chronic disease management and preventive health advice.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "2:00 PM"),
        location = "HealthCare Center"
    ),
    Doctor(
        name = "Dr. Amir Hassan",
        specialty = "Family Medicine",
        rating = 4.8,
        availability = "11:00 AM - 6:00 PM",
        experienceYears = 6,
        patientsCount = "900",
        about = "Dr. Amir focuses on family health, routine check-ups, vaccinations and minor illness treatment.",
        availableTimes = listOf("11:00 AM", "1:00 PM", "4:30 PM"),
        location = "Lim Clinic"
    )
)