package com.example.automate.viewModel

import com.example.automate.model.Vehicle

class VehicleViewModel {
    private val _vehicles = mutableListOf<Vehicle>()
    val vehicles: List<Vehicle> = _vehicles

    fun addVehicle(vehicle: Vehicle) {
        _vehicles.add(vehicle)
    }
}