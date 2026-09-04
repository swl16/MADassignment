package com.example.assignment.database

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE username = :username")
    fun getLocalReminders(username: String): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<Reminder>) // For bulk syncing from cloud

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder) // For saving a single item

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Int)
}