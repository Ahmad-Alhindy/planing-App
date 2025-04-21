package com.example.planingapp.subView

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planingapp.logic.Appointment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
                            val appointmentsInThisTimeSlot = dayAppointments.filter { appointment ->
                                if (appointment.date?.equals(currentDate) == true &&
                                    appointment.startTime != null &&
                                    appointment.endTime != null
                                ) {

                                    val currentTime = time
                                    val startTime = appointment.startTime
                                    val endTime = appointment.endTime

                                    // Same logic you already have
                                    val startsInThisHour = startTime.hour == currentTime.hour
                                    val spansThisHour = startTime.hour < currentTime.hour &&
                                            (endTime.hour > currentTime.hour ||
                                                    (endTime.hour == currentTime.hour && endTime.minute > 0))

                                    startsInThisHour || spansThisHour
                                } else {
                                    false
                                }
                            }
                            // If needed, you can sort them by start time
                            val sortedAppointments =
                                appointmentsInThisTimeSlot.sortedBy { it.startTime }

                            // Then iterate through them with overlap information
                            sortedAppointments.forEach { appointment ->
                                val startTime = appointment.startTime!!
                                val endTime = appointment.endTime!!
                                val currentTime = time

                                val isStartPart = startTime.hour == currentTime.hour
                                val isEndPart = endTime.hour == currentTime.hour

                                // Your existing logic for determining if title should be shown
                                val showTitleInThisSegment = when {
                                    isStartPart && startTime.minute >= 45 -> false
                                    currentTime.hour == startTime.hour + 1 && startTime.minute >= 45 -> true
                                    isStartPart -> true
                                    else -> false
                                }

                                AppointmentCardSegment(
                                    appointment = appointment,
                                    startTime = startTime,
                                    endTime = endTime,
                                    isStartPart = isStartPart,
                                    isEndPart = isEndPart,
                                    showTitle = showTitleInThisSegment,
                                    totalInTimeSlot = appointmentsInThisTimeSlot.size,  // Add this parameter
                                    onClick = {
                                        if (appointmentsByDay.isNotEmpty()) {
                                            onShowAppointmentsForDate(currentDate)
                                        }
                                    }
                                )
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



@Composable
fun AppointmentCardSegment(
    appointment: Appointment,
    startTime: LocalTime?,
    endTime: LocalTime?,
    isStartPart: Boolean,
    isEndPart: Boolean,
    showTitle: Boolean,
    totalInTimeSlot: Int = 1,
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
        color = if (totalInTimeSlot > 1) Color.Red else Color(0xFF4CAF50),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
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
