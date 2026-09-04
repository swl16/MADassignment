package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.EmergencyContact
import com.example.assignment.database.EmergencyContactDao
import com.example.assignment.database.SupabaseService
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmergencyContactViewModel(
    private val emergencyContactDao: EmergencyContactDao
) : ViewModel() {

    private val _contact = MutableStateFlow<EmergencyContact?>(null)
    val contact: StateFlow<EmergencyContact?> = _contact.asStateFlow()

    // 1. Load from Room, then sync latest from Supabase
    fun loadContact(username: String) {
        viewModelScope.launch {
            // Local Room fetch
            val localContact = emergencyContactDao.getForUser(username)
            _contact.value = localContact

            // Background Supabase sync
            try {
                val remoteContact = SupabaseService.client.from("emergency_contacts")
                    .select { filter { eq("username", username) } }
                    .decodeSingleOrNull<EmergencyContact>()

                if (remoteContact != null) {
                    if (localContact != null) {
                        emergencyContactDao.update(remoteContact)
                    } else {
                        emergencyContactDao.insert(remoteContact)
                    }
                    _contact.value = remoteContact
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 2. Save (Insert/Update)
    fun saveContact(contact: EmergencyContact, isExisting: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (isExisting) {
                emergencyContactDao.update(contact)
            } else {
                val generatedId = emergencyContactDao.insert(contact)
                _contact.value = contact.copy(id = generatedId.toInt())
            }

            // Sync to Supabase
            try {
                SupabaseService.client.from("emergency_contacts").upsert(contact)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            onComplete()
        }
    }

    // 3. Delete
    fun deleteContact(contact: EmergencyContact, onComplete: () -> Unit) {
        viewModelScope.launch {
            emergencyContactDao.delete(contact)
            _contact.value = null

            // Sync deletion to Supabase
            try {
                SupabaseService.client.from("emergency_contacts")
                    .delete { filter { eq("username", contact.username) } }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            onComplete()
        }
    }
}