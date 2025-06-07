package com.example.automate.navigation

import AddFuelScreen
import FuelOverviewScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.automate.fuel.ModifyFuelScreen
import com.example.automate.maintenance.AddMaintenanceScreen
import com.example.automate.maintenance.MaintenanceOverviewScreen
import com.example.automate.maintenance.ModifyMaintenanceScreen
import com.example.automate.navigation.Routes.MODIFY_FUEL_SCREEN
import com.example.automate.vehicles.AddVehicleScreen
import com.example.automate.vehicles.ModifyVehicleScreen
import com.example.automate.vehicles.VehicleDetailScreen
import com.example.automate.vehicles.VehiclesScreen
import com.example.automate.viewModel.VehicleViewModel

object Routes {
    const val VEHICLES = "vehicles"
    const val ADD_VEHICLE = "add_vehicle"
    const val VEHICLE_DETAIL = "vehicle_detail"
    const val MODIFY_VEHICLE = "modify_vehicle"
    const val MODIFY_FUEL_SCREEN = "modify_fuel"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    vehicleViewModel: VehicleViewModel
) {

    NavHost(navController, startDestination = Routes.VEHICLES) {
        composable(Routes.VEHICLES) {
            val vehicleList by vehicleViewModel.vehicles.collectAsState()
            VehiclesScreen(
                vehicles = vehicleList,
                onAddVehicleClick = {
                    navController.navigate(Routes.ADD_VEHICLE)
                },
                onVehicleClick = { vehicleId ->
                    navController.navigate("${Routes.VEHICLE_DETAIL}/$vehicleId")
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

        composable("${Routes.VEHICLE_DETAIL}/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")
            val vehicle = vehicleViewModel.getVehicleById(vehicleId.orEmpty())
            if (vehicle != null) {
                VehicleDetailScreen(
                    vehicle = vehicle,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {
                        navController.navigate("modify_vehicle/${vehicle.id}")
                    },
                    onDeleteClick = {
                        vehicleViewModel.removeVehicle(vehicle.id)
                        navController.popBackStack()
                    },
                    navController = navController
                )

            }
        }

        composable(
            route = "modify_vehicle/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")
            val vehicle = vehicleViewModel.getVehicleById(vehicleId.orEmpty())

            if (vehicle != null) {
                ModifyVehicleScreen(
                    vehicleToEdit = vehicle,
                    onSaveClick = { updatedVehicle ->
                        vehicleViewModel.updateVehicle(updatedVehicle)
                        navController.popBackStack()
                    },
                    onCancelClick = { navController.popBackStack() }
                )
            }
        }

        composable("fuel_overview/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            FuelOverviewScreen(
                vehicleId = vehicleId,
                onBackClick = { navController.popBackStack() },
                onAddClick = {
                    navController.navigate("add_fuel/$vehicleId")
                },
                onEditClick = { entry ->
                    navController.navigate("${Routes.MODIFY_FUEL_SCREEN}/$vehicleId/${entry.id}")
                }
            )
        }


        composable("add_fuel/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            AddFuelScreen(
                vehicleId = vehicleId,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }


        composable("$MODIFY_FUEL_SCREEN/{vehicleId}/{entryId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val entryId = backStackEntry.arguments?.getString("entryId") ?: ""

            ModifyFuelScreen(
                vehicleId = vehicleId,
                entryId = entryId,
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable("maintenance_overview/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            MaintenanceOverviewScreen(
                vehicleId = vehicleId,
                onBackClick = { navController.popBackStack() },
                onAddClick = {
                    navController.navigate("add_maintenance/$vehicleId")
                },
                onEditClick = { entry ->
                    navController.navigate("modify_maintenance/${vehicleId}/${entry.id}")
                }
            )
        }

        composable("add_maintenance/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            AddMaintenanceScreen(
                vehicleId = vehicleId,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            "modify_maintenance/{vehicleId}/{entryId}",
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType },
                navArgument("entryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val entryId = backStackEntry.arguments?.getString("entryId") ?: ""

            ModifyMaintenanceScreen(
                vehicleId = vehicleId,
                entryId = entryId,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

    }
}