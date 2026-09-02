package com.example.assignment.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderViewModel: ViewModel(){
    private val repository = ReminderRepository()
    private val supabaseAuth = SupabaseService.client.auth

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    init {
        fetchReminders()
    }

    fun fetchReminders() {
        viewModelScope.launch {
            try {
                val username = supabaseAuth.currentUserOrNull()?.id ?: return@launch

                _reminders.value = repository.getRemindersForUser(username)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                val username = supabaseAuth.currentUserOrNull()?.id ?: return@launch
                val reminderToSave = reminder.copy(username = username)

                if (reminderToSave.id == null) {
                    repository.insertReminder(reminderToSave)
                } else {
                    repository.updateReminder(reminderToSave)
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
                repository.deleteReminder(id)
                fetchReminders()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}