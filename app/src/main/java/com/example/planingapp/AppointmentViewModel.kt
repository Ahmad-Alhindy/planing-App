package com.example.planingapp
import androidx.compose.runtime.mutableStateListOf

import androidx.lifecycle.ViewModel


class AppointmentViewModel : ViewModel() {
    private val _works = mutableStateListOf<Work>()
    val works: List<Work> get() = _works

    fun addAppointment(work: Work) {
        _works.add(work)
    }
}
