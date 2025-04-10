package com.example.planingapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.planingapp.Appointment


@Dao
interface Dao {
    @Query("SELECT * FROM Appointment")
    fun getAllAppointment(): LiveData<List<Appointment>>

    @Query("SELECT * FROM Appointment WHERE isTemplate = 1")
    fun getAllTemplates(): LiveData<List<Appointment>>

    @Query("SELECT * FROM Appointment WHERE isTemplate = 0")
    fun getAllScheduledAppointments(): LiveData<List<Appointment>>

    @Insert
    fun addAppointment(appointment: Appointment)

    @Query("DELETE FROM Appointment WHERE id = :id")
    fun deleteAppointment(id: Int)
}