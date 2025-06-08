package com.example.automate.maintenance

/**
 * Data class representing a single maintenance entry for a specific vehicle.
 *
 * @property id ID of a maintenance entry.
 * @property vehicleId ID of the vehicle this entry is associated with.
 * @property type Type or name of maintenance.
 * @property date Date the maintenance was performed.
 * @property price Total cost of the maintenance.
 * @property odometer Odometer reading at the time of maintenance.
 * @property note Optional note for the entry.
 */
data class MaintenanceEntry(
    val id: String,
    val vehicleId: String,
    val type: String,
    val date: String,
    val price: String,
    val odometer: String,
    val note: String? = null
)
