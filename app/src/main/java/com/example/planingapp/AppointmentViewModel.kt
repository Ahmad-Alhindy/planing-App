package com.example.planingapp
import androidx.compose.runtime.mutableStateListOf

import androidx.lifecycle.ViewModel


class AppointmentViewModel : ViewModel() {
    private val _appointments = mutableStateListOf<Appointment>()
    val Appointments: List<Appointment> get() = _appointments

    fun addAppointment(appointment: Appointment) {
        _appointments.add(appointment)
    }
}
