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
        ).build()
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
                            AddAppoinment(viewModel= viewModel, navController = navController)
                        }
                        composable(nav.calander) {
                            CalendarScreen(viewModel= viewModel)
                        }
                        /*   composable("editTodo/{noteid}") { backStackEntry ->
                            val noteid = backStackEntry.arguments?.getString("noteid")?.toIntOrNull()
                            val todoItem = NoteList.find { it.id == noteid }
                            todoItem?.let {
                                EditNoteScreen(navController = navController, it)}
                        }*/

                    })
            }
        }
    }
}

