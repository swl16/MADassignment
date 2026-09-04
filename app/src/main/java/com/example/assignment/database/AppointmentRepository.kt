package com.example.assignment.database

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppointmentRepository(private val dao: AppointmentDao) {
    private val table = SupabaseService.client.from("appointments")

    fun observeAppointments(username: String): Flow<List<Appointment>> {
        return dao.getAppointmentsForUser(username)
    }

    fun getBookedSlots(doctorName: String, date: String): Flow<List<String>> {
        return dao.getBookedSlotsForDoctor(doctorName, date)
    }

    suspend fun isSlotTaken(doctorName: String, date: String, time: String, excludeId: Int? = null): Boolean {
        return dao.isSlotTaken(doctorName, date, time, excludeId)
    }

    suspend fun syncDoctorSchedule(doctorName: String, date: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteSlots = table.select {
                    filter {
                        eq("doctorName", doctorName)
                        eq("date", date)
                        neq("status", "Canceled")
                    }
                }.decodeList<Appointment>()
                dao.insertAppointments(remoteSlots)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun syncFromRemote(username: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteData = table.select { filter { eq("username", username) } }.decodeList<Appointment>()
                dao.insertAppointments(remoteData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun saveAppointment(appointment: Appointment): Long {
        return withContext(Dispatchers.IO) {
            // 1. Save locally for instant UI update
            val newId = dao.insert(appointment)

            // 2. Attach the generated Room ID before sending to Supabase
            val appointmentWithId = appointment.copy(id = newId.toInt())

            // 3. Sync to cloud
            try {
                table.upsert(appointmentWithId)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            newId // Return the ID so your BookingConfirmedScreen can navigate to it
        }
    }

    suspend fun updateAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            dao.update(appointment)
            try {
                table.update(appointment) { filter { eq("id", appointment.id) } }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            dao.delete(appointment)
            try {
                table.delete { filter { eq("id", appointment.id) } }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}