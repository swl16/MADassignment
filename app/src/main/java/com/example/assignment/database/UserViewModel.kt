package com.example.assignment.viewmodel
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.SupabaseService
import com.example.assignment.database.User
import com.example.assignment.database.UserDao
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

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
                userDao.insertUser(user)

                // Backup to Supabase
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
    fun login(identifier: String, passwordHash: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            // 1. Try local Room DB first — check both email and username login paths
            val localUser = userDao.login(identifier, passwordHash)
                ?: userDao.loginByUsername(identifier, passwordHash)

            if (localUser != null) {
                _errorMessage.value = ""
                onSuccess(localUser.username)
            } else {
                // 2. Not found locally (app might have been reinstalled). Check Supabase!
                try {
                    val remoteUser = SupabaseService.client.from("users")
                        .select {
                            filter {
                                eq("email", identifier)
                                eq("password", passwordHash)
                            }
                        }.decodeSingleOrNull<User>()
                        ?: SupabaseService.client.from("users")
                            .select {
                                filter {
                                    eq("username", identifier)
                                    eq("password", passwordHash)
                                }
                            }.decodeSingleOrNull<User>()

                    if (remoteUser != null) {
                        // 3. Found in the cloud! Restore them to the local Room database
                        userDao.insertUser(remoteUser)
                        _errorMessage.value = ""
                        onSuccess(remoteUser.username)
                    } else {
                        _errorMessage.value = "Invalid email/username or password"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _errorMessage.value =
                        "Invalid email/username or password (or no internet connection)"
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

    fun findUserForReset(identifier: String, onUserVerified: (String) -> Unit) {
        viewModelScope.launch {
            // 1. Try local Room DB first (Email or Username)
            val localUser =
                userDao.getUserByEmail(identifier) ?: userDao.getUserByUsername(identifier)

            if (localUser != null) {
                _errorMessage.value = ""
                onUserVerified(localUser.username)
            } else {
                // 2. Try Supabase cloud fallback
                try {
                    val remoteByEmail = SupabaseService.client.from("users")
                        .select { filter { eq("email", identifier) } }
                        .decodeList<User>()
                        .firstOrNull()

                    val remoteUser = remoteByEmail ?: SupabaseService.client.from("users")
                        .select { filter { eq("username", identifier) } }
                        .decodeList<User>()
                        .firstOrNull()

                    if (remoteUser != null) {
                        // Found in the cloud — restore them to Room locally
                        userDao.insertUser(remoteUser)
                        _errorMessage.value = ""
                        onUserVerified(remoteUser.username)
                    } else {
                        // Genuinely not found — no exception was thrown to get here
                        _errorMessage.value = "User not found. Please check your email or username."
                    }
                } catch (e: Exception) {
                    // Now this only fires on actual network/server failures
                    e.printStackTrace()
                    _errorMessage.value = "Error connecting to server. Please try again later."
                }
            }
        }
    }

    fun uploadProfilePicture(context: Context, uri: Uri, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Create a permanent local copy for offline use
                val extension = "jpg" // Standardize image extension
                val uniqueFileName = "profile_${UUID.randomUUID()}.$extension"
                val localFile = File(context.filesDir, uniqueFileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    localFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Instantly update Room DB and UI with the local offline path
                val currentUserLocal = userDao.getUserByUsername(username)
                if (currentUserLocal != null) {
                    val localUpdatedUser = currentUserLocal.copy(profilePictureUri = localFile.absolutePath)
                    userDao.updateUser(localUpdatedUser)
                    _currentUser.value = localUpdatedUser // Triggers UI redraw instantly

                    // Upload the physical file to the Supabase Storage Bucket
                    val cloudPath = "$username/$uniqueFileName"
                    val bytes = localFile.readBytes()
                    val bucket = SupabaseService.client.storage.from("profile_pictures")

                    bucket.upload(cloudPath, bytes)

                    // 4. Get the public URL and update both databases
                    val publicUrl = bucket.publicUrl(cloudPath)
                    val finalUpdatedUser = localUpdatedUser.copy(profilePictureUri = publicUrl)

                    // Update Local Room DB with Cloud URL
                    userDao.updateUser(finalUpdatedUser)
                    _currentUser.value = finalUpdatedUser

                    // Sync the text URL to the Supabase PostgreSQL users table
                    SupabaseService.client.from("users").update(finalUpdatedUser) {
                        filter { eq("username", username) }
                    }
                }
            } catch (e: Exception) {
                // If offline, the app catches this silently.
                // The local absolutePath remains saved in Room, so the UI still shows the new image.
                e.printStackTrace()
            }
        }
    }



}