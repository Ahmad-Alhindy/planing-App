package com.example.planingapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate


@Composable
fun CalendarScreen(navController: NavController) {
    var currentMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var currentYear by remember { mutableIntStateOf(LocalDate.now().year) }

    val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 = Monday, 7 = Sunday
    val colorStart = Color(0xFF110A25)  // Forest Green
    val colorEnd = Color(0xFF452A4D)
    Column(modifier = Modifier.fillMaxSize() .background(brush = Brush.verticalGradient(listOf(colorStart, colorEnd))),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Month and Year Header with Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }

            Text(
                text = "${firstDayOfMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} $currentYear",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                if (currentMonth == 12) {
                    currentMonth = 1
                    currentYear++
                } else {
                    currentMonth++
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }

        // Days Grid
        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.padding(16.dp)) {
            items(startDayOfWeek - 1) { Spacer(modifier = Modifier.size(40.dp)) } // Empty spaces before the 1st

            items(daysInMonth) { day ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (day + 1).toString(), fontSize = 14.sp)
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    CalendarScreen(navController = rememberNavController())
}