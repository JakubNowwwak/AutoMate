package com.example.automate.fuel

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object FuelStorage {
    private const val FILE_NAME = "fuel_entries.json"

    fun saveEntries(context: Context, entries: List<FuelEntry>) {
        val json = Gson().toJson(entries)
        File(context.filesDir, FILE_NAME).writeText(json)
    }

    fun loadEntries(context: Context): List<FuelEntry> {
        val file = File(context.filesDir, FILE_NAME)
        return if (file.exists()) {
            val json = file.readText()
            Gson().fromJson(json, object : TypeToken<List<FuelEntry>>() {}.type)
        } else emptyList()
    }

    fun getEntriesForVehicle(context: Context, vehicleId: String): List<FuelEntry> {
        return loadEntries(context).filter { it.vehicleId == vehicleId }
    }

    fun addEntry(context: Context, entry: FuelEntry) {
        val all = loadEntries(context).toMutableList()
        all.add(entry)
        saveEntries(context, all)
    }

    fun deleteFuelEntries(context: Context, vehicleId: String) {
        val all = loadEntries(context).toMutableList()
        val filtered = all.filterNot { it.vehicleId == vehicleId }
        saveEntries(context, filtered)
    }
}

