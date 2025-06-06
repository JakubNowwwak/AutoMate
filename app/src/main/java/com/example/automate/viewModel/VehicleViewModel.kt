package com.example.automate.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.automate.data.VehicleStorage
import com.example.automate.fuel.FuelStorage
import com.example.automate.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleViewModel(application: Application) : AndroidViewModel(application) {

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    init {
        _vehicles.value = VehicleStorage.loadVehicles(application)
    }

    fun addVehicle(vehicle: Vehicle) {
        val updated = _vehicles.value + vehicle
        _vehicles.value = updated
        VehicleStorage.saveVehicles(getApplication(), updated)
    }

    fun removeVehicle(vehicleId: String) {
        val updated = _vehicles.value.filter { it.id != vehicleId }
        _vehicles.value = updated
        VehicleStorage.saveVehicles(getApplication(), updated)

        FuelStorage.deleteFuelEntries(getApplication(), vehicleId)
    }

    fun getVehicleById(id: String): Vehicle? {
        return _vehicles.value.find { it.id == id }
    }

    fun updateVehicle(updatedVehicle: Vehicle) {
        val current = _vehicles.value.toMutableList()
        val index = current.indexOfFirst { it.id == updatedVehicle.id }
        if (index != -1) {
            current[index] = updatedVehicle
            _vehicles.value = current
            VehicleStorage.saveVehicles(getApplication(), current)
        }
    }
}