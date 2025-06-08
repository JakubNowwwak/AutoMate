package com.example.automate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.automate.navigation.NavGraph
import com.example.automate.ui.theme.AUTOmateTheme
import com.example.automate.viewModel.VehicleViewModel

/**
 * Main entry point of the AutoMate application.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AUTOmateTheme {
                val navController = rememberNavController()
                val vehicleViewModel: VehicleViewModel by viewModels()
                NavGraph(navController = navController, vehicleViewModel = vehicleViewModel)
            }
        }
    }
}