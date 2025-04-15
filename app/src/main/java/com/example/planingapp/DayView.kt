package com.example.planingapp

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.compose.runtime.Composable

@Composable
fun DayView (navController: NavController) {
    AppScaffold(navController = navController) {
        Text(text = "week View")
    }
}