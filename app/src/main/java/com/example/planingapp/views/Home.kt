package com.example.planingapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.planingapp.NotificationPermissionRequest
import com.example.planingapp.logic.SettingsManger
import com.example.planingapp.logic.Nav

@Composable
fun HomeScreen(navController: NavController) {
    val isDark by SettingsManger.isDarkMode.collectAsState()
    val gradient = if (isDark) {
        Brush.verticalGradient(
            listOf(Color.Black, Color.DarkGray) // Dark mode colors
        )
    } else {
        Brush.verticalGradient(
            listOf(  Color(0xFF1E3A8A),Color(0xFF42A5F5)) // Light mode colors
        )
    }
    NotificationPermissionRequest()
    Box (modifier = Modifier.fillMaxSize()

        .background(gradient),
            contentAlignment = Alignment.Center

    ) {
        Row {
            Button(
                modifier = Modifier,
                onClick = {
                    navController.navigate(Nav.calander)
                },

                colors = ButtonDefaults.buttonColors
                    (containerColor = MaterialTheme.colorScheme.primary)
            )
            { Text("Callander") }

            Button(
                modifier = Modifier.padding(start = 20.dp),
                onClick = {
                    navController.navigate(Nav.makeAnAppointment)
                },

                colors = ButtonDefaults.buttonColors
                    (containerColor = MaterialTheme.colorScheme.primary)
            )
            { Text("Add title") }
        }

    }

}



@Preview(showBackground = true)
@Composable
fun HomeP() {
    HomeScreen(navController = rememberNavController())
}