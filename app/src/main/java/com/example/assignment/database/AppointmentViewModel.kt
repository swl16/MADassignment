package com.example.assignment.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Appointment
import com.example.assignment.database.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppointmentViewModel(private val repository: AppointmentRepository) : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    fun loadAppointments(username: String) {
        viewModelScope.launch {
            // 1. Observe local Room database instantly
            repository.observeAppointments(username).collectLatest { localData ->
                _appointments.value = localData
            }
        }
        // 2. Trigger cloud sync in background
        refreshFromCloud(username)
    }

    private fun refreshFromCloud(username: String) {
        viewModelScope.launch {
            repository.syncFromRemote(username)
        }
    }

    // Notice we accept a callback here so your UI can navigate after saving
    fun saveAppointment(appointment: Appointment, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = repository.saveAppointment(appointment)
            onSuccess(newId)
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.updateAppointment(appointment)
        }
    }

    fun cancelAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.deleteAppointment(appointment)
        }
    }
}