package com.example.assignment.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EmergencyContactDao {
    // 1. Used when saving for the very first time (no existing row yet)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: EmergencyContact): String

    // 2. Used when editing an existing contact
    @Update
    suspend fun update(contact: EmergencyContact)

    // 3. Used by the Delete Contact button
    @Delete
    suspend fun delete(contact: EmergencyContact)

    // 4. Used on screen open, to check whether this user already has a saved contact
    @Query("SELECT * FROM emergency_contacts WHERE username = :username LIMIT 1")
    suspend fun getForUser(username: String): EmergencyContact?
}