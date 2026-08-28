package com.example.assignment.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderViewModel: ViewModel(){
    private val supabase = SupabaseService.client

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    init {
        fetchReminders()
    }

    fun fetchReminders() {
        viewModelScope.launch {
            try {
                val username = supabase.auth.currentUserOrNull()?.id ?: return@launch

                val fetchedReminders = supabase.postgrest["reminders"]
                    .select { filter { eq("username", username) } }
                    .decodeList<Reminder>()

                _reminders.value = fetchedReminders
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                val username = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val reminderToSave = reminder.copy(username = username)

                if (reminderToSave.id == null) {
                    supabase.postgrest["reminders"].insert(reminderToSave)
                } else {
                    supabase.postgrest["reminders"].update(reminderToSave) {
                        filter { eq("id", reminderToSave.id) }
                    }
                }
                fetchReminders() // Refresh list after mutation
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            try {
                supabase.postgrest["reminders"].delete {
                    filter { eq("id", id) }
                }
                fetchReminders()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}