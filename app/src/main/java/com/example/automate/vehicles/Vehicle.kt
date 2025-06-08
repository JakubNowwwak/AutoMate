package com.example.automate.vehicles

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Data class of a vehicle used in JSON database.
 *
 * @property id Unique id for a vehicle.
 * @property brand Brand of a vehicle.
 * @property model Model of a vehícle.
 * @property plate License plate of a vehicle.
 * @property vin Vehicle identification number (VIN), optional.
 * @property currentOdometer Mileage/odometer of a vehicle, optional.
 * @property unit Unit of the odometer of a vehicle, optional.
 * @property registrationDate Age of a vehicle, optional.
 * @property image Path to a saved image of a vehicle, optional.
 */
@Serializable
data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    var brand: String,
    var model: String,
    var plate: String,
    var vin: String? = null,
    var currentOdometer: String = "0",
    var unit: String? = null,
    var registrationDate: String? = null,
    var image: String? = null
)
