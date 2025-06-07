package com.example.automate.fuel

import java.util.UUID


data class FuelEntry(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val date: String,
    val liters: String,
    val price: String,
    val odometer: String? = null,
    val note: String? = null
)

