package com.example.planingapp

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class) // This is needed for CenterAlignedTopAppBar
@Composable
fun AddAppointment(viewModel: AppointmentViewModel, navController: NavController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Date & Time States
    var startTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    val colorStart = Color(0xFF110A25)
    val colorEnd = Color(0xFF452A4D)

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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF110A25)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(brush = Brush.verticalGradient(listOf(colorStart, colorEnd))),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66B266)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Time: $startTime")
            }

            // End Time Picker Button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                onClick = { showTimePicker(context) { hour, minute -> endTime = LocalTime.of(hour, minute) } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66B266)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("End Time: $endTime")
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                        viewModel.addTemplate(newAppointment)
                        navController.popBackStack() // Navigate back after saving
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66B266)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Template", fontSize = 16.sp)
            }

            // Cancel button
            TextButton(
                onClick = { navController.popBackStack() }, // Navigate back without saving
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
            ) {
                Text("Cancel", fontSize = 16.sp, color = Color.White)
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
    val navController = rememberNavController()
    val mockViewModel = AppointmentViewModel()

    AddAppointment(
        viewModel = mockViewModel,
        navController = navController
    )
}
