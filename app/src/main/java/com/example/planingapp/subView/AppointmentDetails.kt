package com.example.planingapp.subView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.planingapp.logic.Appointment
import com.example.planingapp.logic.AppointmentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AppointmentDetails(
    showAppointmentsForDate:  LocalDate?,
    setShowAppointmentsForDate: (LocalDate?) -> Unit,
    appointments: List<Appointment>,
    viewModel: AppointmentViewModel,
) {
    // Appointments Detail Dialog for a specific date
    if (showAppointmentsForDate != null) {
        val dateAppointments = appointments.filter {
            it.date?.equals(showAppointmentsForDate) == true
        }

        Dialog(onDismissRequest = { setShowAppointmentsForDate(null) }) {
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
                                showAppointmentsForDate.format(
                                    DateTimeFormatter.ofPattern("MMMM d, yyyy")
                                )
                            }",
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        IconButton(onClick = { setShowAppointmentsForDate(null) }) {
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
                                    containerColor =   MaterialTheme.colorScheme.primary
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
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )

                                        appointment.description?.let {
                                            if (it.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.secondary
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
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteAppointment(appointment.id)
                                            // If no more appointments, close the dialog
                                            if (dateAppointments.size <= 1) { setShowAppointmentsForDate(null) }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFD02736)
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
    else{
        return
    }
}
