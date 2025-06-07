package com.example.automate.fuel

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.automate.R
import com.example.automate.viewModel.VehicleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ModifyFuelScreen(
    vehicleId: String,
    entryId: String,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    viewModel: VehicleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    println("ModifyFuelScreen: entryId = $entryId")
    println("ModifyFuelScreen: vehicleId = $vehicleId")


    val context = LocalContext.current
    val entry = remember(entryId) { FuelStorage.getEntryById(context, entryId) }
    println("Loaded entry: $entry")

    val vehicle = viewModel.getVehicleById(vehicleId)

    var liters by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var odometer by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(entry) {
        entry?.let {
            liters = it.liters
            price = it.price
            odometer = it.odometer ?: ""
            note = it.note ?: ""
            date = it.date
        }
    }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    var showOdometerError by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            date = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val isInputValid = liters.toFloatOrNull()?.let { it > 0 } == true &&
            price.toFloatOrNull()?.let { it > 0 } == true &&
            odometer.toFloatOrNull()?.let { odo ->
                odo > 0 && odo > (vehicle?.currentOdometer?.toFloatOrNull() ?: 0f)
            } == true

    if (showOdometerError) {
        AlertDialog(
            onDismissRequest = { showOdometerError = false },
            confirmButton = {
                TextButton(onClick = { showOdometerError = false }) {
                    Text("OK")
                }
            },
            title = { Text("Invalid odometer") },
            text = { Text("New odometer value must be greater than the previous one.") }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (entry != null && isInputValid) {
                        val updatedEntry = entry.copy(
                            date = date,
                            liters = liters,
                            price = price,
                            odometer = odometer,
                            note = note.takeIf { it.isNotBlank() }
                        )
                        FuelStorage.updateEntry(context, updatedEntry)

                        if (vehicle != null) {
                            val updatedVehicle = vehicle.copy(currentOdometer = odometer)
                            viewModel.updateVehicle(updatedVehicle)
                        }

                        onSave()
                    } else {
                        showOdometerError = true
                    }
                },
                containerColor = if (isInputValid) MaterialTheme.colorScheme.primary else Color.LightGray,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 40.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Modify Fuel Entry", style = MaterialTheme.typography.headlineMedium)
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        label = { Text("Date") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                OutlinedTextField(
                    value = odometer,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) odometer = it },
                    label = { Text("Odometer (km)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) price = it },
                    label = { Text("Total Price (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = liters,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) liters = it },
                    label = { Text("Liters") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}