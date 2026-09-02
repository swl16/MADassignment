package com.example.assignment.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: ReminderRepository): ViewModel(){
    private val supabaseAuth = SupabaseService.client.auth

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    init {
        fetchReminders()
    }

    fun fetchReminders() {
        viewModelScope.launch {
            val username = supabaseAuth.currentUserOrNull()?.id ?: return@launch

            repository.observeReminders(username).collectLatest {localData -> _reminders.value = localData}

        }
        refreshFromCloud()
    }


    fun refreshFromCloud(){
        viewModelScope.launch {
            val username = supabaseAuth.currentUserOrNull()?.id ?: return@launch
            repository.syncFromRemote(username)
        }
    }

    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            val username = supabaseAuth.currentUserOrNull()?.id ?: return@launch
            repository.saveReminder(reminder.copy(username = username))
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