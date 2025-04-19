package com.example.planingapp.views

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import com.example.planingapp.subView.AppScaffold

@Composable
fun DayView (navController: NavController) {
    AppScaffold(navController = navController) {
        Text(text = "week View")
    }
}