package com.example.planingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planingapp.ui.theme.PlaningAppTheme
import androidx.navigation.compose.NavHost
import androidx.room.Room
import com.example.planingapp.db.AppointmentDb
import com.example.planingapp.logic.AppointmentViewModel
import com.example.planingapp.logic.nav
import com.example.planingapp.views.MakeAppointment
import com.example.planingapp.views.CalendarScreen
import com.example.planingapp.views.DayView
import com.example.planingapp.views.HomeScreen
import com.example.planingapp.views.WeekView


class MainActivity : ComponentActivity() {
    companion object {
        lateinit var appointmentDb: AppointmentDb
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointmentDb = Room.databaseBuilder(
            applicationContext,
            AppointmentDb::class.java,
            AppointmentDb.NAME
        ) .fallbackToDestructiveMigration() // Add this line
            .build()
        enableEdgeToEdge()
        setContent {
            PlaningAppTheme {
                val viewModel = AppointmentViewModel()
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = nav.homeScreen,
                    builder = {
                        composable(nav.homeScreen) {
                            HomeScreen(navController = navController)
                        }
                        composable(nav.addNote) {
                            MakeAppointment(viewModel = viewModel, navController = navController)
                        }
                        composable(nav.calander) {
                            CalendarScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(nav.week) {
                            WeekView(navController = navController, viewModel = viewModel)
                        }
                        composable(nav.day) {
                            DayView(navController = navController)
                        }
                    })
            }
        }
    }
}

