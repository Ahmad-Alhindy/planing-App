package com.example.planingapp
import androidx.lifecycle.LiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppointmentViewModel : ViewModel() {
    val appointmentDao = MainActivity.appointmentDb.getDao()

    val templates: LiveData<List<Appointment>> = appointmentDao.getAllTemplates()
    val scheduledAppointments: LiveData<List<Appointment>> = appointmentDao.getAllScheduledAppointments()

    fun addAppointment(appointment: Appointment) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentDao.addAppointment(appointment)
        }
    }

    fun addTemplate(appointment: Appointment) {
        viewModelScope.launch(Dispatchers.IO) {
            // Ensure it's marked as a template and has no date
            val template = appointment.copy(isTemplate = true, date = null)
            appointmentDao.addAppointment(template)
        }
    }


    fun deleteAppointment(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentDao.deleteAppointment(id)
        }
    }

    fun deleteTemplate(template: Appointment) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentDao.deleteAppointment(template.id)
        }
    }
}