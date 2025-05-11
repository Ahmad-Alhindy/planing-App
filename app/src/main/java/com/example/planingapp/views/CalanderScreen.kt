package com.example.planingapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import com.example.planingapp.logic.AppointmentViewModel
import com.example.planingapp.logic.SettingsManger
import com.example.planingapp.logic.Nav
import com.example.planingapp.logic.scheduleAppointmentReminders
import com.example.planingapp.subView.AppScaffold
import com.example.planingapp.subView.AppointmentDetails
import com.example.planingapp.subView.CalendarGrid
import com.example.planingapp.subView.CalendarHeader
import com.example.planingapp.subView.TemplateDialog
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(viewModel: AppointmentViewModel, navController: NavController) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val appointments by viewModel.scheduledAppointments.asFlow().collectAsState(emptyList())
    val templates by viewModel.templates.asFlow().collectAsState(emptyList())

    val context = LocalContext.current
    val isDark by SettingsManger.isDarkMode.collectAsState()
    val gradient = if (isDark) {
        Brush.verticalGradient(
            listOf(Color.Black, Color.DarkGray) // Dark mode colors
        )
    } else {
        Brush.verticalGradient(
            listOf( Color(0xFF1E3A8A),Color(0xFF42A5F5)))
    }
    // Dialog states
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showAppointmentsForDate by remember { mutableStateOf<LocalDate?>(null) }
    AppScaffold(navController = navController) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            // Calendar part
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                // Month and Year Header with Navigation Buttons
                CalendarHeader(
                    currentYearMonth = currentYearMonth,
                    onPreviousMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
                    onNextMonth = { currentYearMonth = currentYearMonth.plusMonths(1) }
                )

                // Calendar grid for day selection
                CalendarGrid(
                    currentYearMonth = currentYearMonth,
                    appointments = appointments,
                    onDateSelected = { date ->
                        selectedDate = date
                        showTemplateDialog = true  // Show the dialog when a date is selected
                    },
                    onShowAppointmentsForDate = { date ->
                        showAppointmentsForDate = date
                    }
                )
            }

            // Floating Action Button to create new template or add from existing
            FloatingActionButton(
                onClick = {
                    if (templates.isEmpty()) {
                        // Navigate to AddAppointment if no templates exist
                        navController.navigate(Nav.makeAnAppointment)
                    } else {
                        // Show template selection with today's date
                        selectedDate = LocalDate.now()
                        showTemplateDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Appointment",
                    tint = Color.White
                )
            }


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


            // Appointments Detail Dialog for a specific date
            AppointmentDetails(
                showAppointmentsForDate = showAppointmentsForDate,
                setShowAppointmentsForDate = { showAppointmentsForDate = it },
                appointments = appointments,
                viewModel = viewModel
            )
        }
    }
}