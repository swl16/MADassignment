package com.example.assignment.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.temporal.TemporalQueries.localDate

class ReminderViewModel(private val repository: ReminderRepository): ViewModel(){

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private var currentUsername: String = ""

    fun setUsername(username: String) {
        currentUsername = username
    }

    fun fetchReminders() {
        if (currentUsername.isBlank()) return
        viewModelScope.launch {
            repository.observeReminders(currentUsername).collect { localData ->
                _reminders.value = localData
            }
        }
    }

    fun syncReminders() {
        if (currentUsername.isBlank()) return
        viewModelScope.launch {
            repository.syncFromRemote(currentUsername)
        }
    }


    fun saveReminder(reminder: Reminder, username: String) {
        viewModelScope.launch {
            repository.saveReminder(reminder.copy(username= username))
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteReminder(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}