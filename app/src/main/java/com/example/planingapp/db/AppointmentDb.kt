package com.example.planingapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.planingapp.logic.Appointment


@Database(entities = [Appointment::class], version = 3)
@TypeConverters(Converters::class)  // Add this line
abstract class AppointmentDb : RoomDatabase() {
    companion object{
        const val NAME = "AppointmentDb"
    }

    abstract fun getDao() : Dao
}