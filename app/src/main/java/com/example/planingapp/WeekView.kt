package com.example.planingapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun WeekView(navController: NavController) {
    AppScaffold(navController = navController) {
        Text(text = "week View")
    }
}