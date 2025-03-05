package com.example.planingapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Composable
fun addAppoinment(viewModel: AppointmentViewModel, navController: NavController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(BusinessType.Work) }

    // Date & Time States
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    val colorStart = Color(0xFF110A25)  // Forest Green
    val colorEnd = Color(0xFF452A4D)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(colorStart, colorEnd))),
    verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
                .padding(start= 20.dp, top = 50.dp, end = 150.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(12.dp))

        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
                .padding(start= 20.dp, top = 0.dp, end = 150.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        // Date Picker
        Button(modifier = Modifier.padding(start= 20.dp),
            onClick = { showDatePicker(context) { selectedDate = it } },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF66B266)  // Use hex color #90EE90
            )
        ) {
            Text("Select Date: $selectedDate")
        }

        // Start Time Picker
        // Start Time Picker Button
        Button(modifier = Modifier.padding(start= 20.dp),
            onClick = { showTimePicker(context) { hour, minute -> startTime = LocalTime.of(hour, minute) } },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66B266))  // Set button color
        ) {
            Text("Start Time: $startTime")
        }

// End Time Picker Button
        Button(modifier = Modifier.padding(start= 20.dp),
            onClick = { showTimePicker(context) { hour, minute -> endTime = LocalTime.of(hour, minute) } },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF66B266)
            )  // Set button color
        ) {
            Text("End Time: $endTime")
        }

        // Dropdown for selecting type
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = Modifier.padding(start= 20.dp),
                onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF66B266)
                    )) {
                Text(selectedType.name)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                BusinessType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        // Save button
        Button(
            onClick = {
                val newAppointment = Work(
                    title = title,
                    description = description,
                    date = selectedDate,
                    startTime = startTime,
                    endTime = endTime,
                    type = selectedType
                )
                viewModel.addAppointment(newAppointment)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().padding(start = 100.dp,end = 100.dp),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF66B266)
        )
        ){
            Text("Save")
        }
    }
}

// Date Picker Function
fun showDatePicker(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
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
fun PreviewNoteScreen() {
    // Provide a mock NavController and ViewModel
    val navController = rememberNavController()
    val mockViewModel = AppointmentViewModel()

    addAppoinment(
        viewModel = mockViewModel, // Mock ViewModel
        navController = navController,
    )
}
