package com.example.assignment.database


import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderRepository {
    private val supabase = SupabaseService.client
    private val table = supabase.postgrest["reminders"]

    // suspend functions replace standard DAO queries
    suspend fun getRemindersForUser(username: String): List<Reminder> {
        return withContext(Dispatchers.IO) {
            table.select { filter { eq("username", username) } }.decodeList<Reminder>()
        }
    }

    suspend fun insertReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            table.insert(reminder)
        }
    }

    suspend fun updateReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            table.update(reminder) { filter { eq("username", reminder.username) } }
        }
    }


    suspend fun deleteReminder(id: String) {
        withContext(Dispatchers.IO) {
            table.delete { filter { eq("username", id) } }
        }
    }
}