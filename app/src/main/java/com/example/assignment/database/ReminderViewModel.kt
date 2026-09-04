package com.example.assignment.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.temporal.TemporalQueries.localDate

class ReminderViewModel(private val repository: ReminderRepository): ViewModel(){

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    fun loadReminders(username : String){
        viewModelScope.launch{
            repository.observeReminders(username).collectLatest { localData ->
                _reminders.value = localData
            }
        }
        refreshFromCloud(username)
    }

    fun refreshFromCloud(username:String){
        viewModelScope.launch {
            repository.syncFromRemote(username)
        }
    }

    fun saveReminder(reminder: Reminder, username: String) {
        viewModelScope.launch {
            repository.saveReminder(reminder.copy(username= username))
        }
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteReminder(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}