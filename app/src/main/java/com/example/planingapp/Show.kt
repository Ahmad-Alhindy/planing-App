package com.example.planingapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.planingapp.ui.theme.PlaningAppTheme


@Composable
fun Show(navController: NavController) {
    Text( "Show Screen ")
    Column (modifier = Modifier.padding(8.dp))
    {

    }


}


@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    Show(navController = rememberNavController())
}