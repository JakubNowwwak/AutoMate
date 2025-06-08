package com.example.automate.maintenance

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import java.util.UUID

@Composable
fun AddMaintenanceScreen(
    vehicleId: String,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    viewModel: VehicleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(stringResource(R.string.dd_mm_yyyy), Locale.getDefault())

    var maintenanceType by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var odometer by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(dateFormat.format(calendar.time)) }

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

    val isInputValid = maintenanceType.isNotBlank()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isInputValid) {
                        val entry = MaintenanceEntry(
                            id = UUID.randomUUID().toString(),
                            vehicleId = vehicleId,
                            type = maintenanceType,
                            date = date,
                            price = price,
                            odometer = odometer,
                            note = note.takeIf { it.isNotBlank() }
                        )
                        MaintenanceStorage.addEntry(context, entry)
                        viewModel.updateVehicleOdometerIfHigher(vehicleId, odometer)
                        onSave()
                    }
                },
                containerColor = if (isInputValid) MaterialTheme.colorScheme.primary else Color.LightGray
            ) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.add_maintenance), style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = maintenanceType,
                onValueChange = { maintenanceType = it },
                label = { Text(stringResource(R.string.maintenance_type)) },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text( stringResource(R.string.date)) },
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
                value = price,
                onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) price = it },
                label = { Text(stringResource(R.string.price)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = odometer,
                onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) odometer = it },
                label = { Text(stringResource(R.string.odometer_km)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.note_optional)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
