package com.example.automate.data

import android.content.Context
import com.example.automate.model.Vehicle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object VehicleStorage {

    private const val FILE_NAME = "vehicles.json"

    fun saveVehicles(context: Context, vehicles: List<Vehicle>) {
        val json = Gson().toJson(vehicles)
        File(context.filesDir, FILE_NAME).writeText(json)
    }

    fun loadVehicles(context: Context): List<Vehicle> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<Vehicle>>() {}.type
        return Gson().fromJson(json, type)
    }
}
