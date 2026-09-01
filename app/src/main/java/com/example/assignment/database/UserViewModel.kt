package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.SupabaseService
import com.example.assignment.database.User
import com.example.assignment.database.UserDao
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun clearError() {
        _errorMessage.value = ""
    }

    // Handles SignUpScreen Logic
    fun signUp(user: User, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val existingEmail = userDao.getUserByEmail(user.email)
            val existingUsername = userDao.getUserByUsername(user.username)

            if (existingEmail != null) {
                _errorMessage.value = "Email is already registered!"
            } else if (existingUsername != null) {
                _errorMessage.value = "Username is already used by others!"
            } else {
                // 1. Save locally to Room
                userDao.insertUser(user)

                // 2. Backup to Supabase
                try {
                    SupabaseService.client.from("users").insert(user)
                } catch (e: Exception) {
                    e.printStackTrace() // If offline, Room still works
                }

                _errorMessage.value = ""
                onSuccess(user.username)
            }
        }
    }

    // Handles LoginPage Logic
// Handles LoginPage Logic with Cloud Fallback
    fun login(email: String, passwordHash: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            // 1. Try to find the user in the local Room DB first
            val localUser = userDao.login(email, passwordHash)

            if (localUser != null) {
                // Found locally!
                _errorMessage.value = ""
                onSuccess(localUser.username)
            } else {
                // 2. Not found locally (app might have been reinstalled). Check Supabase!
                try {
                    val remoteUser = SupabaseService.client.from("users")
                        .select {
                            filter {
                                eq("email", email)
                                eq("password", passwordHash)
                            }
                        }.decodeSingleOrNull<User>()

                    if (remoteUser != null) {
                        // 3. Found in the cloud! Restore them to the local Room database
                        userDao.insertUser(remoteUser)

                        _errorMessage.value = ""
                        onSuccess(remoteUser.username)
                    } else {
                        // Not in Room and not in Supabase
                        _errorMessage.value = "Invalid email or password"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Usually triggers if the user has no internet and Room is empty
                    _errorMessage.value = "Invalid email or password (or no internet connection)"
                }
            }
        }
    }

    // Handles ProfileScreen Logic
    fun loadUserProfile(username: String) {
        viewModelScope.launch {
            // 1. Instantly load from local Room DB
            val localUser = userDao.getUserByUsername(username)
            _currentUser.value = localUser

            // 2. Fetch latest updates from Supabase silently in background
            try {
                val remoteUser = SupabaseService.client.from("users")
                    .select { filter { eq("username", username) } }
                    .decodeSingleOrNull<User>()

                if (remoteUser != null) {
                    userDao.updateUser(remoteUser) // Update local cache
                    _currentUser.value = remoteUser // Update UI
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Add this inside UserViewModel class
    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            // 1. Instantly update local Room DB
            userDao.updateUser(user)
            _currentUser.value = user // Update UI state

            // 2. Sync to Supabase in the background
            try {
                SupabaseService.client.from("users")
                    .update(user) {
                        filter { eq("username", user.username) }
                    }
            } catch (e: Exception) {
                e.printStackTrace() // If offline, Room still has the updated data
            }
        }
    }

    // Add this inside UserViewModel class
    fun changeUserPassword(username: String, newPasswordHash: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Get the current user from Room
            val currentUser = userDao.getUserByUsername(username)

            if (currentUser != null) {
                // 2. Update the password
                val updatedUser = currentUser.copy(password = newPasswordHash)

                // 3. Save to local Room DB
                userDao.updateUser(updatedUser)

                // 4. Sync to Supabase in the background
                try {
                    SupabaseService.client.from("users")
                        .update(updatedUser) {
                            filter { eq("username", username) }
                        }
                } catch (e: Exception) {
                    e.printStackTrace() // If offline, Room still saves it
                }

                // 5. Trigger navigation
                onSuccess()
            }
        }
    }
}