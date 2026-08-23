package com.example.assignment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    // 1. Used when "Confirm appointment" is tapped — returns the new row's id
    //    so the confirmation screen can navigate straight to it.
    @Insert
    suspend fun insert(appointment: Appointment): Long

    // 2. Used by AppointmentScreen to show this user's appointment list (auto-updates UI)
    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY id DESC")
    fun getAppointmentsForUser(userId: Int): Flow<List<Appointment>>

    // 3. Used by BookingConfirmedScreen to re-fetch the appointment just created
    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getById(id: Int): Appointment?
}