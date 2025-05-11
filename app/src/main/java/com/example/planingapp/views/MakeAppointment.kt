package com.example.planingapp.views

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planingapp.logic.Appointment
import com.example.planingapp.logic.AppointmentViewModel
import com.example.planingapp.logic.SettingsManger
import com.example.planingapp.logic.scheduleAppointmentReminders
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class) // This is needed for CenterAlignedTopAppBar
@Composable
fun MakeAppointment(  viewModel: AppointmentViewModel? = null,
                      navController: NavController? = null,
                      selectedDate: LocalDate? = null
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Date & Time States
    var startTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    val isDark by SettingsManger.isDarkMode.collectAsState()
    val gradient = if (isDark) {
        Brush.verticalGradient(
            listOf(Color.Black, Color.DarkGray) // Dark mode colors
        )
    } else {
        Brush.verticalGradient(
            listOf( Color(0xFF1E3A8A),Color(0xFF42A5F5)))
    }
    LaunchedEffect(startTime) {
        if (endTime.isBefore(startTime) || endTime == startTime) {
            endTime = startTime.plusHours(1)
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Appointment Template",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDark) Color.Black else Color(0xFF1E3A8A)
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(gradient),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = selectedDate?.toString() ?: "",
                color = Color.White,
                modifier = Modifier.padding(start = 25.dp)
            )
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            // Start Time Picker
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                onClick = { showTimePicker(context) { hour, minute -> startTime = LocalTime.of(hour, minute) } },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Time: $startTime", color = MaterialTheme.colorScheme.secondary)
            }

            // End Time Picker Button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                onClick = {
                    showTimePicker(context) { hour, minute ->
                        val selectedTime = LocalTime.of(hour, minute)
                        if (!selectedTime.isBefore(startTime)) {
                            endTime = selectedTime
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("End Time: $endTime", color = MaterialTheme.colorScheme.secondary)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedDate != null){
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newAppointment = Appointment(
                                title = title,
                                description = description,
                                startTime = startTime,
                                endTime = endTime,
                                date = selectedDate,
                                isTemplate = false
                            )
                            viewModel?.addAppointment(newAppointment)
                            scheduleAppointmentReminders(context, newAppointment)
                            navController?.popBackStack() // Navigate back after saving
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 40.dp, top = 20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Appointment", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }
            // Save button with back navigation
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newAppointment = Appointment(
                            title = title,
                            description = description,
                            startTime = startTime,
                            endTime = endTime,
                            isTemplate = true
                        )
                        viewModel?.addTemplate(newAppointment)
                        navController?.popBackStack() // Navigate back after saving
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Template", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
            }

            // Cancel button
            TextButton(
                onClick = { navController?.popBackStack() }, // Navigate back without saving
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
            ) {
                Text("Cancel", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

// Time Picker Function
fun showTimePicker(context: Context, onTimeSelected: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            onTimeSelected(hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

@Preview(showBackground = true)
@Composable
fun PreviewAddAppointment() {
    MakeAppointment() // No parameters needed
}