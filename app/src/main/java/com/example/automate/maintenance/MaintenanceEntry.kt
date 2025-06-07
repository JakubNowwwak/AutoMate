package com.example.automate.maintenance

data class MaintenanceEntry(
    val id: String,
    val vehicleId: String,
    val type: String,
    val date: String,
    val price: String,
    val odometer: String,
    val note: String? = null
)
