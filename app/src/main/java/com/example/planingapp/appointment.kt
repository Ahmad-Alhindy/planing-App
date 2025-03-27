package com.example.planingapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime
@Entity
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0 ,
    val title: String,
    val description: String? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
)