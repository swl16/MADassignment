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

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    // 3. Used in SignUpScreen to make sure the email isn't already taken
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    // 5. Used in Edit Profile to save changes
    @androidx.room.Update
    suspend fun updateUser(user: User)
}