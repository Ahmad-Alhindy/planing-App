package com.example.planingapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.planingapp.logic.nav

@Composable
fun HomeScreen(navController: NavController) {
    val colorStart = Color(0xFF110A25)  // Forest Green
    val colorEnd = Color(0xFF452A4D)
    Box (modifier = Modifier.fillMaxSize()

        .background(brush = Brush.verticalGradient(listOf(colorStart, colorEnd))),
            contentAlignment = Alignment.Center

    ) {
        Row {
            Button(
                modifier = Modifier,
                onClick = {
                    navController.navigate(nav.calander)
                },

                colors = ButtonDefaults.buttonColors
                    (containerColor = Color(0xFF4CAF50))
            )
            { Text("Callander") }

            Button(
                modifier = Modifier.padding(start = 20.dp),
                onClick = {
                    navController.navigate(nav.addNote)
                },

                colors = ButtonDefaults.buttonColors
                    (containerColor = Color(0xFF4CAF50))
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