package com.example.assignment.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    // 1. Used when "Confirm appointment" is tapped — returns the new row's id
    //    so the confirmation screen can navigate straight to it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: Appointment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<Appointment>)

    // 2. Used by AppointmentScreen to show this user's appointment list (auto-updates UI)
    @Query("SELECT * FROM appointments WHERE username = :username ORDER BY id DESC")
    fun getAppointmentsForUser(username: String): Flow<List<Appointment>>

    // 3. Used by BookingConfirmedScreen to re-fetch the appointment just created
    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getById(id: String): Appointment?

    @Update
    suspend fun update(appointment: Appointment)

    @Delete
    suspend fun delete(appointment: Appointment)

    @Query("""
        SELECT time FROM appointments 
        WHERE doctorName = :doctorName 
          AND date = :date 
          AND status != 'Canceled'
    """)
    fun getBookedSlotsForDoctor(doctorName: String, date: String): Flow<List<String>>

    // 2. Check if a specific slot is taken (exclude current appointment ID during rescheduling)
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM appointments 
            WHERE doctorName = :doctorName 
              AND date = :date 
              AND time = :time 
              AND status != 'Canceled'
              AND (:excludeId IS NULL OR id != :excludeId)
        )
    """)
    suspend fun isSlotTaken(
        doctorName: String,
        date: String,
        time: String,
        excludeId: String? = null
    ): Boolean
}