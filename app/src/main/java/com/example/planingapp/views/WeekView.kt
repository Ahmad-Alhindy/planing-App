package com.example.planingapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import com.example.planingapp.logic.Appointment
import com.example.planingapp.logic.AppointmentViewModel
import com.example.planingapp.logic.SettingsManger
import com.example.planingapp.logic.scheduleAppointmentReminders
import com.example.planingapp.subView.AppScaffold
import com.example.planingapp.subView.AppointmentDetails
import com.example.planingapp.subView.DaysOfWeekHeader
import com.example.planingapp.subView.TemplateDialog
import com.example.planingapp.subView.WeekGrid
import com.example.planingapp.subView.WeekHeader
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeekView(
    viewModel: AppointmentViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val isDark by SettingsManger.isDarkMode.collectAsState()
    val gradient = if (isDark) {
        Brush.verticalGradient(
            listOf(Color.Black, Color.DarkGray) // Dark mode colors
        )
    } else {
        Brush.verticalGradient(
            listOf(  Color(0xFF1E3A8A),Color(0xFF42A5F5)) // Light mode colors
        )
    }

    AppScaffold(navController = navController) {
        var showAppointmentsForDate by remember { mutableStateOf<LocalDate?>(null) }
        var showTemplateDialog by remember { mutableStateOf(false) }
        val templates by viewModel.templates.asFlow().collectAsState(emptyList())
        var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

        // State for selected week
        var currentWeekStart by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }
        val appointments by viewModel.scheduledAppointments.asFlow().collectAsState(emptyList())

        // Get appointments for the current week
        val weekAppointments = remember(appointments, currentWeekStart) {
            filterAppointmentsForWeek(appointments, currentWeekStart)
        }

        Column(modifier = Modifier.fillMaxSize().background(gradient)) {
            // Week header with navigation
            WeekHeader(
                weekStart = currentWeekStart,
                onPreviousWeek = { currentWeekStart = currentWeekStart.minusWeeks(1) },
                onNextWeek = { currentWeekStart = currentWeekStart.plusWeeks(1) }
            )

            // Days of week header
            DaysOfWeekHeader(weekStart = currentWeekStart)

            WeekGrid(
                weekStart = currentWeekStart,
                appointments = weekAppointments,
                onDateSelected = { date ->
                    selectedDate = date
                    showTemplateDialog = true
                },
                onShowAppointmentsForDate = { date ->
                    showAppointmentsForDate = date
                }
            )
        }
        // Appointments Detail Dialog for a specific date
        AppointmentDetails(
            showAppointmentsForDate = showAppointmentsForDate,
            setShowAppointmentsForDate = { showAppointmentsForDate = it },
            appointments = appointments,
            viewModel = viewModel
        )
        // Template Selection Dialog
        TemplateDialog(
            showDialog = showTemplateDialog,
            onDismiss = { showTemplateDialog = false },
            templates = templates,
            selectedDate = selectedDate,
            onTemplateSelected = { template, date ->
                // Create a new appointment from template
                val newAppointment = template.copy(
                    id = 0, // Will be auto-assigned
                    date = date,
                    isTemplate = false
                )
                viewModel.addAppointment(newAppointment)
                scheduleAppointmentReminders(context, newAppointment)

                showTemplateDialog = false
            },
            onCreateNewTemplate = {
                navController.navigate("makeAnAppointment?date=${selectedDate?.toString()}")
            },
            onDeleteTemplate = { template ->
                viewModel.deleteTemplate(template)
            }
        )
    }
}




// Helper functions

fun getStartOfWeek(date: LocalDate): LocalDate {
    return date.with(DayOfWeek.MONDAY)
}

fun filterAppointmentsForWeek(appointments: List<Appointment>, weekStart: LocalDate): List<Appointment> {
    val weekEnd = weekStart.plusDays(6)
    val filteredAppointments = appointments.filter { appointment ->
        val appointmentDate = appointment.date
        // Only include appointments with non-null dates that fall within the week
        appointmentDate != null && !appointmentDate.isBefore(weekStart) && !appointmentDate.isAfter(weekEnd)
    }

    // Debug: log the filtered appointments
    println("Filtered ${filteredAppointments.size} appointments for week starting $weekStart")

    return filteredAppointments
}
