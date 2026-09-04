package com.example.assignment.appointment

// ---------- Shared doctor data (hardcoded for now — swap for a DoctorDao later if needed) ----------

data class Doctor(
    val id: String,
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
    // --- General Medicine ---
    Doctor(
        id = "doc_1",
        name = "Dr. Sarah Lim",
        specialty = "General",
        rating = 4.9,
        availability = "9:00 AM - 4:00 PM",
        experienceYears = 8,
        patientsCount = "1.5k",
        about = "Primary care practitioner specializing in preventative medicine, executive health screenings, lifestyle health planning, and routine diagnostics.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "2:00 PM", "3:30 PM"),
        location = "HealthCare Central Clinic"
    ),
    Doctor(
        id = "doc_2",
        name = "Dr. David Miller",
        specialty = "General",
        rating = 4.7,
        availability = "9:00 AM - 3:30 PM",
        experienceYears = 6,
        patientsCount = "920",
        about = "Focuses on acute illness management, travel vaccinations, blood panel analysis, and family wellness checkups.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "2:00 PM"),
        location = "MediPoint Health Hub"
    ),

    // --- Cardiology ---
    Doctor(
        id = "doc_3",
        name = "Dr. Marcus Tan",
        specialty = "Cardiology",
        rating = 4.9,
        availability = "9:00 AM - 2:00 PM",
        experienceYears = 14,
        patientsCount = "2.8k",
        about = "Senior Consultant Cardiologist focusing on coronary artery health, hypertension management, heart failure diagnostics, and preventive cardiology.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "2:00 PM"),
        location = "City Heart & Vascular Center"
    ),
    Doctor(
        id = "doc_4",
        name = "Dr. Elena Rostova",
        specialty = "Cardiology",
        rating = 4.8,
        availability = "10:30 AM - 4:00 PM",
        experienceYears = 11,
        patientsCount = "1.9k",
        about = "Clinical cardiologist with specialized experience in echocardiography evaluations, arrhythmias, and post-cardiac rehabilitation guidance.",
        availableTimes = listOf("10:30 AM", "2:00 PM", "3:30 PM"),
        location = "Metro Heart Institute"
    ),

    // --- Dental ---
    Doctor(
        id = "doc_5",
        name = "Dr. Chloe Wong",
        specialty = "Dental",
        rating = 4.8,
        availability = "10:00 AM - 5:00 PM",
        experienceYears = 7,
        patientsCount = "1.1k",
        about = "Practices comprehensive family dentistry, restorative fillings, root canal therapy, teeth whitening, and orthodontic consultations.",
        availableTimes = listOf("10:30 AM", "2:00 PM", "3:30 PM"),
        location = "Smile Studio Dental"
    ),
    Doctor(
        id = "doc_6",
        name = "Dr. Benjamin Hayes",
        specialty = "Dental",
        rating = 4.9,
        availability = "9:00 AM - 3:30 PM",
        experienceYears = 10,
        patientsCount = "1.6k",
        about = "Oral surgeon and aesthetic dentist with a focus on wisdom tooth extractions, dental implants, and preventive periodontal therapy.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "3:30 PM"),
        location = "Premier Dental Center"
    ),

    // --- Pediatrics ---
    Doctor(
        id = "doc_7",
        name = "Dr. Amir Hassan",
        specialty = "Pediatrics",
        rating = 5.0,
        availability = "9:00 AM - 3:30 PM",
        experienceYears = 12,
        patientsCount = "2.2k",
        about = "Dedicated pediatrician managing newborn development, childhood vaccination schedules, asthma management, and common pediatric conditions.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "2:00 PM"),
        location = "Little Stars Children's Clinic"
    ),
    Doctor(
        id = "doc_8",
        name = "Dr. Jessica Patel",
        specialty = "Pediatrics",
        rating = 4.9,
        availability = "10:00 AM - 4:00 PM",
        experienceYears = 9,
        patientsCount = "1.4k",
        about = "Specializes in adolescent medicine, early childhood allergy assessments, and developmental milestone checkups.",
        availableTimes = listOf("10:30 AM", "2:00 PM", "3:30 PM"),
        location = "Family Care Medical"
    ),

    // --- Neurology ---
    Doctor(
        id = "doc_9",
        name = "Dr. Priya Nair",
        specialty = "Neurology",
        rating = 4.8,
        availability = "1:00 PM - 5:00 PM",
        experienceYears = 13,
        patientsCount = "1.7k",
        about = "Neurologist providing consultations for chronic migraines, peripheral nerve disorders, tension headaches, and sleep disturbances.",
        availableTimes = listOf("2:00 PM", "3:30 PM"),
        location = "Metro Neuro & Spine Institute"
    ),
    Doctor(
        id = "doc_10",
        name = "Dr. Jonathan Vance",
        specialty = "Neurology",
        rating = 4.7,
        availability = "9:00 AM - 2:00 PM",
        experienceYears = 15,
        patientsCount = "2.1k",
        about = "Specialist in neuro-degenerative care, vertigo diagnostics, cognitive health evaluations, and spinal rehabilitation.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "2:00 PM"),
        location = "Apex Neurological Hospital"
    ),

    // --- Dermatology ---
    Doctor(
        id = "doc_11",
        name = "Dr. Hannah Chen",
        specialty = "Dermatology",
        rating = 4.9,
        availability = "10:00 AM - 4:00 PM",
        experienceYears = 8,
        patientsCount = "1.3k",
        about = "Specialist in inflammatory skin conditions, acne management, laser therapies, eczema treatments, and skin health maintenance.",
        availableTimes = listOf("10:30 AM", "2:00 PM", "3:30 PM"),
        location = "Dermacare Skin Clinic"
    ),

    // --- Orthopedics ---
    Doctor(
        id = "doc_12",
        name = "Dr. Robert Sterling",
        specialty = "Orthopedics",
        rating = 4.8,
        availability = "9:00 AM - 3:30 PM",
        experienceYears = 11,
        patientsCount = "1.8k",
        about = "Orthopedic consultant focusing on joint health, sports injury rehabilitation, knee and shoulder pain management, and bone density health.",
        availableTimes = listOf("9:00 AM", "10:30 AM", "2:00 PM", "3:30 PM"),
        location = "Joint & Bone Specialist Hospital"
    )
)