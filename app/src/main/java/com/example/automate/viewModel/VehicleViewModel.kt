package com.example.automate.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.automate.vehicles.VehicleStorage
import com.example.automate.fuel.FuelStorage
import com.example.automate.maintenance.MaintenanceStorage
import com.example.automate.vehicles.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel that manages vehicle data and operations.
 *
 * @constructor Initializes the ViewModel and loads saved vehicles from local storage.
 */
class VehicleViewModel(application: Application) : AndroidViewModel(application) {

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    init {
        _vehicles.value = VehicleStorage.loadVehicles(application)
    }

    /**
     * Adds a new vehicle to the list.
     *
     * @param vehicle New vehicle to add.
     */
    fun addVehicle(vehicle: Vehicle) {
        val updated = _vehicles.value + vehicle
        _vehicles.value = updated
        VehicleStorage.saveVehicles(getApplication(), updated)
    }

    /**
     * Removes a vehicle by its ID and deletes all related fuel and maintenance entries.
     *
     * @param vehicleId ID of a vehicle to remove.
     */
    fun removeVehicle(vehicleId: String) {
        val updated = _vehicles.value.filter { it.id != vehicleId }
        _vehicles.value = updated
        VehicleStorage.saveVehicles(getApplication(), updated)

        FuelStorage.deleteFuelEntries(getApplication(), vehicleId)

        MaintenanceStorage.deleteEntriesForVehicle(getApplication(), vehicleId)
    }

    /**
     * Returns vehicle by its ID.
     *
     * @param id ID of a vehicle.
     * @return The matching Vehicle.
     */
    fun getVehicleById(id: String): Vehicle? {
        return _vehicles.value.find { it.id == id }
    }

    /**
     * Updates an existing vehicle
     *
     * @param updatedVehicle Updated vehicle object.
     */
    fun updateVehicle(updatedVehicle: Vehicle) {
        val current = _vehicles.value.toMutableList()
        val index = current.indexOfFirst { it.id == updatedVehicle.id }
        if (index != -1) {
            current[index] = updatedVehicle
            _vehicles.value = current
            VehicleStorage.saveVehicles(getApplication(), current)
        }
    }

    /**
     * Updates odometer of a vehicle if it is higher than the current one.
     *
     * @param vehicleId ID of a vehicle to update.
     * @param newOdometer New odometer reading as a string.
     */
    fun updateVehicleOdometerIfHigher(vehicleId: String, newOdometer: String) {
        val vehicle = getVehicleById(vehicleId) ?: return
        val current = vehicle.currentOdometer?.toFloatOrNull() ?: 0f
        val incoming = newOdometer.toFloatOrNull() ?: 0f

        if (incoming > current) {
            val updated = vehicle.copy(currentOdometer = newOdometer)
            updateVehicle(updated)
        }
    }

}