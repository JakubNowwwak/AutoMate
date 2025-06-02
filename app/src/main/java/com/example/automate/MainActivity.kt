package com.example.automate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.automate.navigation.NavGraph
import com.example.automate.ui.theme.AUTOmateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AUTOmateTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
