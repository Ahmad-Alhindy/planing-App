package com.example.planingapp.subView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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

