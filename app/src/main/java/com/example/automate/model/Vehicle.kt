package com.example.automate.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    var brand: String,
    var model: String,
    var plate: String,
    var vin: String? = null,
    var mileage: String? = null,
    var unit: String? = null,
    var registrationDate: String? = null,
    var image: String? = null
)
