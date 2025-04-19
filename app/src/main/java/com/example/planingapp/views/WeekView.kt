package com.example.planingapp.views
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import com.example.planingapp.logic.Appointment
import com.example.planingapp.logic.AppointmentViewModel
import com.example.planingapp.logic.nav
import com.example.planingapp.subView.AppScaffold
import com.example.planingapp.subView.AppointmentDetails
import com.example.planingapp.subView.TemplateDialog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeekView(
    viewModel: AppointmentViewModel,
    navController: NavController
) {

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

        Column(modifier = Modifier.fillMaxSize()) {
            // Week header with navigation
            WeekHeader(
                weekStart = currentWeekStart,
                onPreviousWeek = { currentWeekStart = currentWeekStart.minusWeeks(1) },
                onNextWeek = { currentWeekStart = currentWeekStart.plusWeeks(1) }
            )

            // Days of week header
            DaysOfWeekHeader(weekStart = currentWeekStart)

            // Week grid with time slots and appointments
            WeekGrid(
                weekStart = currentWeekStart,
                appointments = weekAppointments,
                onShowAppointmentsForDate = { date ->
                    showAppointmentsForDate = date
                },
                onDateSelected = { date ->
                    selectedDate = date
                    showTemplateDialog = true  // Show the dialog when a date is selected
                },

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
                showTemplateDialog = false
            },
            onCreateNewTemplate = {
                navController.navigate(nav.addNote)
            },
            onDeleteTemplate = { template ->
                viewModel.deleteTemplate(template)
            }
        )
        // Load appointments when the view is first displayed
        /*  LaunchedEffect(Unit) {
              viewModel.loadAppointments()
          }*/
    }
}

@Composable
fun WeekHeader(
    weekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val weekEndDate = weekStart.plusDays(6)

    val headerText = if (weekStart.month == weekEndDate.month) {
        "${weekStart.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${weekStart.year}"
    } else {
        "${weekStart.format(DateTimeFormatter.ofPattern("MMM d"))} - ${weekEndDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(onClick = onPreviousWeek) {
           Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Week"
            )
        }

        Text(
            text = headerText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        IconButton(onClick = onNextWeek) {
           Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Week"
            )
        }
    }
}

@Composable
fun DaysOfWeekHeader(weekStart: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 4.dp)
    ) {
        // Time column placeholder
        Box(modifier = Modifier.width(60.dp))

        // Day columns with dividers between them
        for (dayOffset in 0..6) {
            val date = weekStart.plusDays(dayOffset.toLong())
            val isToday = date.equals(LocalDate.now())

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isToday) 24.sp else 18.sp,
                    color = if (isToday) Color.Red else Color.Black
                )

                Text(
                    text = date.dayOfMonth.toString(),
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color.Red else Color.Black,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(Color.Transparent)
                        .padding(4.dp)
                )
            }
        }
    }
}



@Composable
fun WeekGrid(
    weekStart: LocalDate,
    appointments: List<Appointment>,
    onDateSelected: (LocalDate) -> Unit,
    onShowAppointmentsForDate: (LocalDate) -> Unit
) {
    // Display time slots from 6 AM to 11 PM
    val timeSlots = (6..23).map { LocalTime.of(it, 0) }

    // Group appointments by day
    val appointmentsByDay = appointments
        .filter { it.date != null && it.startTime != null && it.endTime != null }
        .groupBy { appointment: Appointment ->
            appointment.date!!.dayOfWeek
        }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(timeSlots) { time ->
            Column {
                // Time slot row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    // Time indicator
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = time.format(DateTimeFormatter.ofPattern("h a")),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Day columns
                    for (dayOffset in 0..6) {
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            color = Color.Black,
                            thickness = 1.dp
                        )
                        val currentDate = weekStart.plusDays(dayOffset.toLong())
                        val dayOfWeek = currentDate.dayOfWeek
                        val dayAppointments = appointmentsByDay[dayOfWeek] ?: emptyList()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 2.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            onDateSelected(currentDate)
                                        }
                                    )
                                }
                        ) {
                            // Draw appointments for this time slot and day
                            dayAppointments.forEach { appointment ->
                                if (appointment.date?.equals(currentDate) == true &&
                                    appointment.startTime != null &&
                                    appointment.endTime != null) {

                                    val currentTime = time
                                    val startTime = appointment.startTime
                                    val endTime = appointment.endTime

                                    // Improved overlap detection logic
                                    val startsInThisHour = startTime.hour == currentTime.hour
                                    val spansThisHour = startTime.hour < currentTime.hour &&
                                            (endTime.hour > currentTime.hour ||
                                                    (endTime.hour == currentTime.hour && endTime.minute > 0))

                                    if (startsInThisHour || spansThisHour) {
                                        val isStartPart = startTime.hour == currentTime.hour
                                        val isEndPart = endTime.hour == currentTime.hour

                                        // Determine if title should be shown in this segment
                                        val showTitleInThisSegment = when {
                                            // First segment but starts too late in the hour
                                            isStartPart && startTime.minute >= 45 -> false

                                            // Second segment and previous segment was too small
                                            currentTime.hour == startTime.hour + 1 && startTime.minute >= 45 -> true

                                            // Normal first segment
                                            isStartPart -> true

                                            // All other segments
                                            else -> false
                                        }

                                        AppointmentCardSegment(
                                            appointment = appointment,
                                            startTime = startTime,
                                            endTime = endTime,
                                            isStartPart = isStartPart,
                                            isEndPart = isEndPart,
                                            showTitle = showTitleInThisSegment,
                                            onClick = {
                                                if (appointmentsByDay.isNotEmpty()) {
                                                    onShowAppointmentsForDate(currentDate)  // Use this callback instead
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Divider between time slots
                HorizontalDivider(
                    color = Color.Black,
                    thickness = 1.1.dp
                )
            }
        }
    }
}

@Composable
fun AppointmentCardSegment(
    appointment: Appointment,
    startTime: LocalTime?,
    endTime: LocalTime?,
    isStartPart: Boolean,
    isEndPart: Boolean,
    showTitle: Boolean,
    onClick: () -> Unit
) {
    // Calculate top padding for start segments
    val topPadding = if (isStartPart) {
        (startTime!!.minute.toFloat() / 60f * 40f).dp
    } else {
        0.dp
    }

    // Calculate the height fraction for end segments
    val heightFraction = if (isEndPart) {
        when (endTime!!.minute) {
            0 -> 0f  // End exactly on the hour
            15 -> 0.25f
            30 -> 0.5f
            45 -> 0.75f
            else -> endTime.minute.toFloat() / 60f
        }
    } else {
        1f  // Fill the entire slot for middle segments
    }

    // Skip rendering if it's an end segment that ends exactly on the hour
    if (isEndPart && endTime!!.minute == 0) {
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(heightFraction)
            .padding(top = topPadding)
            .padding(horizontal = 2.dp),
        color = Color(0xFF4CAF50),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Show title based on showTitle parameter
            if (showTitle) {
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
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
