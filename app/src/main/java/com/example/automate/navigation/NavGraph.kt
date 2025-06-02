package com.example.automate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.automate.vehicles.AddVehicleScreen
import com.example.automate.vehicles.VehiclesScreen

object Routes {
    const val VEHICLES = "vehicles"
    const val ADD_VEHICLE = "add_vehicle"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Routes.VEHICLES) {
        composable(Routes.VEHICLES) {
            VehiclesScreen(
                onAddVehicleClick = {
                    navController.navigate(Routes.ADD_VEHICLE)
                },
                onVehicleClick = { vehicleId ->
                    //TODO Implement vehicle click
                }
            )
        }
        composable(Routes.ADD_VEHICLE) {
            AddVehicleScreen(
                onSaveClick = {
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
