package com.example.planingapp
import androidx.lifecycle.LiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AppointmentViewModel : ViewModel() {
    val appointmentDao = MainActivity.appointmentDb.getDao()

    val appointments : LiveData<List<Appointment>> = appointmentDao.getAllAppointment()

    fun addAppointment(appointment: Appointment) {
        viewModelScope.launch (Dispatchers.IO){
            appointmentDao.addAppointment(appointment)
        }

    }

    fun deletAppoinment(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentDao.deleteAppointment(id)
        }
    }
}
