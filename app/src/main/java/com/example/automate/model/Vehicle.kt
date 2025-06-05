package com.example.automate.model

class Vehicle (
    val id: String = java.util.UUID.randomUUID().toString(),
    var brand: String,
    var model: String,
    var plate: String,
    var image: String? = null,
)

