package com.example.planingapp

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.asFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun CalendarScreen(viewModel: AppointmentViewModel) {
    var currentMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var currentYear by remember { mutableIntStateOf(LocalDate.now().year) }
    val appointments by viewModel.appointments.asFlow().collectAsState(emptyList())
    val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 = Monday, 7 = Sunday
    val colorStart = Color(0xFF110A25)  // Forest Green
    val colorEnd = Color(0xFF452A4D)
    var longClickedAppointment by remember { mutableStateOf<Appointment?>(null) }
   Box( modifier = Modifier.fillMaxSize()
       .zIndex(1f)
       .background(brush = Brush.verticalGradient(listOf(colorStart, colorEnd))),
       ) {
       Row(
           modifier = Modifier.padding(top = 30.dp, start = 10.dp)
               .zIndex(1f)
               .horizontalScroll(rememberScrollState())
       ) {
           appointments.forEach { appointment ->
               // Use a Column for each appointment
               Column(
                   modifier = Modifier
                       .padding(8.dp) // Space between items
                       .background(Color(0xFF4CAF50))
                       .pointerInput(Unit) {
                           detectTapGestures(
                               onLongPress = {
                                   longClickedAppointment =
                                       appointment // Set the long-clicked appointment
                               }
                           )
                       }
               ) {
                   // Display the type at the top
                   Text(
                       text = appointment.title.toString(),
                       fontWeight = FontWeight.Bold,
                       fontSize = 16.sp
                   )

                   // Add space between type and times
                   Spacer(modifier = Modifier.height(2.dp))

                   val startTimeFormatted =
                       appointment.startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                           ?: "N/A"
                   val endTimeFormatted =
                       appointment.endTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                           ?: "N/A"
                   Text(
                       text = "$startTimeFormatted, $endTimeFormatted",
                       fontSize = 14.sp
                   )
                   longClickedAppointment?.let {
                       if (it == appointment) {
                           Button(
                               onClick = {
                                   viewModel.deletAppoinment(appointment.id) // Call the delete function
                                   longClickedAppointment = null // Reset the long-click state
                               },
                               modifier = Modifier.padding(top = 8.dp)
                           ) {
                               Text("Delete")
                           }
                       }
                   }
               }
           }
       }
       Column( modifier = Modifier
           .fillMaxWidth()
           .zIndex(0f)
           .padding(top = 70.dp)) {
           // Month and Year Header with Navigation Buttons
           Row(
               modifier = Modifier
                   .fillMaxWidth()
                   .zIndex(0f)
                   .padding(top = 16.dp),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               IconButton(onClick = {
                   if (currentMonth == 1) {
                       currentMonth = 12
                       currentYear -= 1
                   } else {
                       currentMonth--
                   }
               }) {
                   Icon(
                       Icons.AutoMirrored.Filled.ArrowBack,
                       contentDescription = "Previous Month",
                       tint = Color.White
                   )
               }

               Text(
                   text = "${
                       firstDayOfMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                   } $currentYear",
                   fontSize = 20.sp,
                   fontWeight = FontWeight.Bold,
                   color = Color.White // Change to desired color
               )

               IconButton(onClick = {
                   if (currentMonth == 12) {
                       currentMonth = 1
                       currentYear++
                   } else {
                       currentMonth++
                   }
               }) {
                   Icon(
                       Icons.AutoMirrored.Filled.ArrowForward,
                       contentDescription = "Next Month",
                       tint = Color.White
                   )
               }
           }

           // Days Grid
           LazyVerticalGrid(
               columns = GridCells.Fixed(4),
               verticalArrangement = Arrangement.spacedBy(5.dp) // Adds space between rows
           ) {
               items(startDayOfWeek - 1) {} // Empty spaces before the 1st

               items(daysInMonth) { day ->
                   val currentDate = firstDayOfMonth.plusDays(day.toLong()) // Get the correct date
                   val dayLetter =
                       currentDate.dayOfWeek.name.lowercase().first() // Extract first letter

                   Column(
                       modifier = Modifier
                           .height(80.dp)
                           .background(Color.LightGray),
                       horizontalAlignment = Alignment.CenterHorizontally

                   ) {
                       Text(
                           text = dayLetter.toString(),
                           fontSize = 18.sp
                       ) // Day letter (e.g., "M")
                       Spacer(modifier = Modifier.height(15.dp))
                       Text(text = (day + 1).toString(), fontSize = 18.sp) // Day number
                   }
               }
           }
       }
   }


}

@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    val mockViewModel = AppointmentViewModel()
    CalendarScreen(viewModel = mockViewModel)
}