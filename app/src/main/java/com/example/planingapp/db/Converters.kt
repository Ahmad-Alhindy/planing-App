package com.example.planingapp.db

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(formatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, formatter) }
    }
}