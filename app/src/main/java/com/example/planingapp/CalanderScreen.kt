package com.example.planingapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(viewModel: AppointmentViewModel, navController: NavController) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val appointments by viewModel.scheduledAppointments.asFlow().collectAsState(emptyList())
    val templates by viewModel.templates.asFlow().collectAsState(emptyList())
    val colorStart = Color(0xFF110A25)
    val colorEnd = Color(0xFF452A4D)

    // Dialog states
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showAppointmentsForDate by remember { mutableStateOf<LocalDate?>(null) }
    AppScaffold {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(listOf(colorStart, colorEnd)))
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
                        navController.navigate(nav.addNote)
                    } else {
                        // Show template selection with today's date
                        selectedDate = LocalDate.now()
                        showTemplateDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Appointment",
                    tint = Color.White
                )
            }

            // Template Selection Dialog
            if (showTemplateDialog && selectedDate != null) {
                Dialog(onDismissRequest = { showTemplateDialog = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Appointment",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                IconButton(onClick = { showTemplateDialog = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                            )

                            Text(
                                text = "Date: ${selectedDate?.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (templates.isEmpty()) {
                                Text(
                                    text = "No appointment available. Create one first!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                )

                                Button(
                                    onClick = {
                                        showTemplateDialog = false
                                        navController.navigate(nav.addNote)
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF4CAF50
                                        )
                                    )
                                ) {
                                    Text("Create New Appointment")
                                }
                            } else {
                                LazyColumn {
                                    items(templates) { template ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF5F5F5))
                                                .clickable {
                                                    // Schedule the selected template for the date
                                                    selectedDate?.let { date ->
                                                        viewModel.scheduleAppointment(
                                                            template,
                                                            date
                                                        )
                                                        showTemplateDialog = false
                                                    }
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = template.title,
                                                    style = MaterialTheme.typography.titleMedium
                                                )

                                                template.description?.let {
                                                    if (it.isNotBlank()) {
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = it,
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Row {
                                                    val startTime = template.startTime?.format(
                                                        DateTimeFormatter.ofPattern("HH:mm")
                                                    ) ?: "N/A"
                                                    val endTime = template.endTime?.format(
                                                        DateTimeFormatter.ofPattern("HH:mm")
                                                    ) ?: "N/A"

                                                    Text(
                                                        text = "$startTime - $endTime",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                            IconButton(onClick = { viewModel.deleteTemplate(template) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete template",
                                                    tint = Color.Red

                                                )
                                            }
                                        }
                                    }

                                    item {
                                        Button(
                                            onClick = {
                                                showTemplateDialog = false
                                                navController.navigate(nav.addNote)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(
                                                    0xFF4CAF50
                                                )
                                            )
                                        ) {
                                            Text("Create New Appointment")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Appointments Detail Dialog for a specific date
            if (showAppointmentsForDate != null) {
                val dateAppointments = appointments.filter {
                    it.date?.equals(showAppointmentsForDate) == true
                }

                Dialog(onDismissRequest = { showAppointmentsForDate = null }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Appointments for ${
                                        showAppointmentsForDate?.format(
                                            DateTimeFormatter.ofPattern("MMMM d, yyyy")
                                        )
                                    }",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 18.sp
                                )

                                IconButton(onClick = { showAppointmentsForDate = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            LazyColumn {
                                items(dateAppointments) { appointment ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFE8F5E9)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = appointment.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                appointment.description?.let {
                                                    if (it.isNotBlank()) {
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = it,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                val startTime = appointment.startTime?.format(
                                                    DateTimeFormatter.ofPattern("HH:mm")
                                                ) ?: "N/A"
                                                val endTime = appointment.endTime?.format(
                                                    DateTimeFormatter.ofPattern("HH:mm")
                                                ) ?: "N/A"

                                                Text(
                                                    text = "$startTime - $endTime",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteAppointment(appointment.id)
                                                    // If no more appointments, close the dialog
                                                    if (dateAppointments.size <= 1) {
                                                        showAppointmentsForDate = null
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Delete",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}