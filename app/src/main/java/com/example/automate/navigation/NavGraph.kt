package com.example.automate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.automate.vehicles.AddVehicleScreen
import com.example.automate.vehicles.VehiclesScreen
import com.example.automate.viewModel.VehicleViewModel

object Routes {
    const val VEHICLES = "vehicles"
    const val ADD_VEHICLE = "add_vehicle"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val vehicleViewModel = VehicleViewModel()

    NavHost(navController, startDestination = Routes.VEHICLES) {
        composable(Routes.VEHICLES) {
            VehiclesScreen(
                vehicles = vehicleViewModel.vehicles,
                onAddVehicleClick = {
                    navController.navigate(Routes.ADD_VEHICLE)
                }
            )
        }
        composable(Routes.ADD_VEHICLE) {
            AddVehicleScreen(
                onSaveClick = { vehicle ->
                    vehicleViewModel.addVehicle(vehicle)
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
