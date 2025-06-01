package com.example.automate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.automate.ui.theme.AUTOmateTheme
import com.example.automate.vehicles.VehiclesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AUTOmateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VehiclesScreen()
                }
            }
        }
    }
}