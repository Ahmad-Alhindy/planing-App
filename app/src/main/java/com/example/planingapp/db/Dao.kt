package com.example.planingapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.planingapp.Appointment


@Dao
interface Dao {

    @Query("Select * from Appointment")
    fun getAllAppointment() : LiveData<List<Appointment>>

    @Insert
    fun addAppointment(appointment: Appointment)

    @Query("Delete from Appointment where id = :id")
    fun deleteAppointment(id: Int)
}