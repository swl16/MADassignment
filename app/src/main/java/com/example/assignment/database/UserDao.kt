package com.example.assignment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // 1. Used in SignUpScreen to save a new account
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // 2. Used in LoginPage to verify credentials
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    // 3. Used in SignUpScreen to make sure the email isn't already taken
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    // 4. Used in Profile to display the current logged-in user
    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    suspend fun getLatestUser(): User?

    // 5. Used in Edit Profile to save changes
    @androidx.room.Update
    suspend fun updateUser(user: User)
}