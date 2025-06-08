package com.example.automate.vehicles

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * Object responsible for storing and retrieving vehicle data.
 * Vehicles are saved in JSON file.
 */
object VehicleStorage {

    private const val FILE_NAME = "vehicles.json"

    /**
     * Saves vehicles to internal storage in JSON file.
     *
     * @param context Android context for accessing internal storage.
     * @param vehicles List of vehicles to be saved.
     */
    fun saveVehicles(context: Context, vehicles: List<Vehicle>) {
        val json = Gson().toJson(vehicles)
        File(context.filesDir, FILE_NAME).writeText(json)
    }

    /**
     * Loads list of vehicles from internal storage.
     *
     * @param context Android context for accessing internal storage.
     * @return List of saved vehicles.
     */
    fun loadVehicles(context: Context): List<Vehicle> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<Vehicle>>() {}.type
        return Gson().fromJson(json, type)
    }
}