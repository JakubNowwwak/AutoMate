package com.example.automate.fuel

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

/**
 * Object responsible for managing fuel entries
 * Fuel entries are stored in a JSON file in internal storage.
 */
object FuelStorage {
    private const val FILE_NAME = "fuel_entries.json"

    /**
     * Loads all fuel entries and returns only those associated with the specified vehicle.
     *
     * @param context Android context used to access internal storage.
     * @param vehicleId IF of a vehicle to load fuel entries for.
     * @return A list of entries for a specific vehicle.
     */
    fun getEntriesForVehicle(context: Context, vehicleId: String): List<FuelEntry> {
        val all = loadEntries(context)
        return all.filter { it.vehicleId == vehicleId }
    }

    /**
     * Gets a single fuel entry by ID.
     *
     * @param context Android context used to access internal storage.
     * @param entryId IF of a fuel entry to load.
     * @return Object of a fuel entry or null if not found.
     */
    fun getEntryById(context: Context, entryId: String): FuelEntry? {
        return loadEntries(context).find { it.id == entryId }
    }

    /**
     * Adds new fuel entry to the internal storage.
     *
     * @param context Android context used to access internal storage.
     * @param entry FuelEntry to add.
     */
    fun addEntry(context: Context, entry: FuelEntry) {
        val entries = loadEntries(context).toMutableList()
        entries.add(entry.copy(id = UUID.randomUUID().toString()))
        saveEntries(context, entries)
    }

    /**
     * Updates an existing fuel entry.
     *
     * @param context Android context used to access internal storage.
     * @param updatedEntry Modified [uelEntry to save.
     */
    fun updateEntry(context: Context, updatedEntry: FuelEntry) {
        val entries = loadEntries(context).map {
            if (it.id == updatedEntry.id) updatedEntry else it
        }
        saveEntries(context, entries)
    }

    /**
     * Loads all fuel entries from internal JSON file.
     *
     * @param context Android context used to access internal storage.
     * @return A list of all  FuelEntry objects.
     */
    private fun loadEntries(context: Context): List<FuelEntry> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        return try {
            val json = file.readText()
            val type = object : TypeToken<List<FuelEntry>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Saves entries into JSON file.
     *
     * @param context Android context used to access internal storage.
     * @param entries The list of FuelEntries.
     */
    private fun saveEntries(context: Context, entries: List<FuelEntry>) {
        val json = Gson().toJson(entries)
        File(context.filesDir, FILE_NAME).writeText(json)
    }

    /**
     * Deletes all fuel entries associated with a given vehicle.
     *
     * @param context Android context used to access internal storage.
     * @param vehicleId Vehicle ID to delete entries for.
     */
    fun deleteFuelEntries(context: Context, vehicleId: String) {
        val entries = loadEntries(context).filterNot { it.vehicleId == vehicleId }
        saveEntries(context, entries)
    }

}


