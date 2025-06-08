package com.example.automate.maintenance

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

/**
 * Object used for managing maintenance entries.
 */
object MaintenanceStorage {
    private const val FILE_NAME = "maintenance_entries.json"

    /**
     * Returns a list of maintenance entries for a specific vehicle.
     *
     * @param context Android context used to access file.
     * @param vehicleId ID of the vehicle whose entries belong to.
     * @return List of MaintenanceEntry objects related to the given vehicle.
     */
    fun getEntriesForVehicle(context: Context, vehicleId: String): List<MaintenanceEntry> {
        val all = loadEntries(context)
        return all.filter { it.vehicleId == vehicleId }
    }

    /**
     * Returns maintenance entry by ID.
     *
     * @param context Android context used to access file.
     * @param entryId ID of the maintenance entry.
     * @return MaintenanceEntry object.
     */
    fun getEntryById(context: Context, entryId: String): MaintenanceEntry? {
        return loadEntries(context).find { it.id == entryId }
    }

    /**
     * Adds a new maintenance entry.
     *
     * @param context Android context used to access file.
     * @param entry MaintenanceEntry object to be added.
     */
    fun addEntry(context: Context, entry: MaintenanceEntry) {
        val entries = loadEntries(context).toMutableList()
        entries.add(entry.copy(id = UUID.randomUUID().toString()))
        saveEntries(context, entries)
    }

    /**
     * Updates an existing maintenance entry
     *
     * @param context Android context used to access file.
     * @param updatedEntry MaintenanceEntry containing updated information
     */
    fun updateEntry(context: Context, updatedEntry: MaintenanceEntry) {
        val entries = loadEntries(context).map {
            if (it.id == updatedEntry.id) updatedEntry else it
        }
        saveEntries(context, entries)
    }

    /**
     * Deletes all maintenance entries of a specific vehicle.
     *
     * @param context Android context used to access file.
     * @param vehicleId ID of the vehicle whose entries should be deleted.
     */
    fun deleteEntriesForVehicle(context: Context, vehicleId: String) {
        val entries = loadEntries(context).filterNot { it.vehicleId == vehicleId }
        saveEntries(context, entries)
    }

    /**
     * Loads all maintenance entries from JSON file.
     *
     * @param context Android context
     * @return List of MaintenanceEntry objects.
     */
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

    /**
     * Saves maintenance entries to JSON file.
     *
     * @param context Android context used to access file.
     * @param entries List of MaintenanceEntry objects to be saved.
     */
    private fun saveEntries(context: Context, entries: List<MaintenanceEntry>) {
        val json = Gson().toJson(entries)
        File(context.filesDir, FILE_NAME).writeText(json)
    }
}