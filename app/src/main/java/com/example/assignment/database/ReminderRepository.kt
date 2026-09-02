package com.example.assignment.database


import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReminderRepository(private val dao: ReminderDao) {
    private val supabase = SupabaseService.client
    private val table = supabase.postgrest["reminders"]

    fun observeReminders(username: String): Flow<List<Reminder>> {
        return dao.getLocalReminders(username)
    }

    suspend fun syncFromRemote(username: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteData = table.select { filter { eq("username", username) } }.decodeList<Reminder>()
                dao.insertReminders(remoteData) // Updates local DB, which automatically updates Compose UI
            } catch (e: Exception) {
                // Ignore network errors if offline, UI will just show local cache
            }
        }
    }

    suspend fun saveReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            dao.insertReminder(reminder) // Save locally first for instant feedback
            try {
                table.upsert(reminder) // 'Upsert' inserts if new, updates if existing
            } catch (e: Exception) {
                // If offline, the item stays in Room and can be synced later
                e.printStackTrace()
            }
        }
    }


    suspend fun deleteReminder(id: String) {
        withContext(Dispatchers.IO) {
            dao.deleteReminder(id)

            try {
                table.delete { filter { eq("id", id) } }
            }catch(e: Exception){
                e.printStackTrace()
            }
        }
    }
}