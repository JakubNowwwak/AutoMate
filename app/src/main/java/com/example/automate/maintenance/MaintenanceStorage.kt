package com.example.automate.maintenance

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

object MaintenanceStorage {
    private const val FILE_NAME = "maintenance_entries.json"

    fun getEntriesForVehicle(context: Context, vehicleId: String): List<MaintenanceEntry> {
        val all = loadEntries(context)
        return all.filter { it.vehicleId == vehicleId }
    }

    fun getEntryById(context: Context, entryId: String): MaintenanceEntry? {
        return loadEntries(context).find { it.id == entryId }
    }

    fun addEntry(context: Context, entry: MaintenanceEntry) {
        val entries = loadEntries(context).toMutableList()
        entries.add(entry.copy(id = UUID.randomUUID().toString()))
        saveEntries(context, entries)
    }

    fun updateEntry(context: Context, updatedEntry: MaintenanceEntry) {
        val entries = loadEntries(context).map {
            if (it.id == updatedEntry.id) updatedEntry else it
        }
        saveEntries(context, entries)
    }

    fun deleteEntriesForVehicle(context: Context, vehicleId: String) {
        val entries = loadEntries(context).filterNot { it.vehicleId == vehicleId }
        saveEntries(context, entries)
    }

    private fun loadEntries(context: Context): List<MaintenanceEntry> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        return try {
            val json = file.readText()
            val type = object : TypeToken<List<MaintenanceEntry>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveEntries(context: Context, entries: List<MaintenanceEntry>) {
        val json = Gson().toJson(entries)
        File(context.filesDir, FILE_NAME).writeText(json)
    }
}