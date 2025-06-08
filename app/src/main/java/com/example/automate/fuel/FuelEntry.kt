package com.example.automate.fuel

import java.util.UUID

/**
 * Data class representing a fuel entry.
 *
 * @property id ID of an entry.
 * @property vehicleId ID of the vehicle to which fuel entry belongs.
 * @property date Date of the fuel entry in "dd.MM.yyyy" format.
 * @property liters Amount of fuel refilled.
 * @property price Total price of refuel.
 * @property odometer Odometer reading at the time of refuel.
 * @property note Optional note for this fuel entry.
 */
data class FuelEntry(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val date: String,
    val liters: String,
    val price: String,
    val odometer: String? = null,
    val note: String? = null
)
