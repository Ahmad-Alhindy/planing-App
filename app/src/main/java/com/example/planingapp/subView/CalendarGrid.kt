package com.example.planingapp.subView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planingapp.logic.Appointment
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarGrid(
    currentYearMonth: YearMonth,
    appointments: List<Appointment>,
    onDateSelected: (LocalDate) -> Unit,
    onShowAppointmentsForDate: (LocalDate) -> Unit  // Add this callback

) {
    // Calculate dates for the month grid
    val firstDayOfMonth = currentYearMonth.atDay(1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 = Monday, 7 = Sunday


    // Build calendar grid
    // Days Grid
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        items(startDayOfWeek - 1) {
            // Empty spaces before the 1st
            Box(modifier = Modifier.height(120.dp))
        }

        items(daysInMonth) { day ->
            val currentDate = firstDayOfMonth.plusDays(day.toLong())
            val dateAppointments = appointments.filter {
                it.date?.equals(currentDate) == true
            }
            val isToday = currentDate == LocalDate.now()

            // The calendar cell
            Box(
                modifier = Modifier
                    .height(138.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isToday) Color(0x33FFFFFF) else Color(0x22FFFFFF)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onDateSelected(currentDate)
                            },
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    // Day number at the top
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (day + 1).toString(),
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Display appointments for this date
                    if (dateAppointments.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            dateAppointments.take(1).forEach { appointment ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.clickable {
                                            onShowAppointmentsForDate(currentDate)  // Use this callback instead
                                        }
                                    ) {
                                        Text(
                                            text = appointment.title,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        val startTime =
                                            appointment.startTime?.format(
                                                DateTimeFormatter.ofPattern("HH:mm")
                                            ) ?: "N/A"
                                        val endTime = appointment.endTime?.format(
                                            DateTimeFormatter.ofPattern("HH:mm")
                                        ) ?: "N/A"
                                        Text(
                                            text = "$startTime - $endTime",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // If there are more than 2 appointments
                            if (dateAppointments.size > 1) {
                                Text(
                                    text = "+${dateAppointments.size - 1} more",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(top = 2.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}