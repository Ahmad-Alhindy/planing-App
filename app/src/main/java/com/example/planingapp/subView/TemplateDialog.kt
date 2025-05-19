package com.example.planingapp.subView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.planingapp.logic.Appointment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TemplateDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    templates: List<Appointment>,
    selectedDate: LocalDate?,
    onTemplateSelected: (Appointment, LocalDate) -> Unit,
    onCreateNewTemplate: () -> Unit,
    onDeleteTemplate: (Appointment) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dialog title
                        Text(
                            text = "Select Appointment",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        IconButton(onClick = { onDismiss() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                    )

                    Text(
                        text = "Date: ${selectedDate?.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // Template list
                    if (templates.isEmpty()) {
                        Text(
                            text = "No appointment available. Create one first!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        // Using height constraint instead of weight to prevent excessive space
                        Box(
                            modifier = Modifier
                                .heightIn(max = 300.dp) // Set a max height for the list
                                .fillMaxWidth()
                        ) {
                            LazyColumn {
                                items(templates) { template ->
                                    TemplateItem(
                                        template = template,
                                        selectedDate = selectedDate,
                                        onSelect = { appointment, date ->
                                            if (date != null) onTemplateSelected(appointment, date)
                                        },
                                        onDelete = onDeleteTemplate
                                    )
                                }
                            }
                        }
                    }

                    // Create new template button - now outside and below the conditional block
                    // This ensures it's always visible at the bottom
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (templates.isEmpty()) {
                                onDismiss()
                            }
                            onCreateNewTemplate()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Create New Appointment",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}



@Composable
private fun TemplateItem(
    template: Appointment,
    selectedDate: LocalDate?,
    onSelect: (Appointment, LocalDate?) -> Unit,
    onDelete: (Appointment) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(12.dp)
        .clickable { onSelect(template, selectedDate) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = template.title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary

            )
            template.description?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary

                    )
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "${template.startTime} - ${template.endTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary

            )
        }

        // Delete button
        IconButton(onClick = { onDelete(template) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete template",
                tint = Color(0xFFD02736)
            )
        }
    }
}
