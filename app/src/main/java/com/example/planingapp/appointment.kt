package com.example.planingapp

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

enum class BusinessType { Work, pappaLedigt }
data class Work(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val date: LocalDate,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val type: BusinessType
)