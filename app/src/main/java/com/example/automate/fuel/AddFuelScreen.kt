import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.automate.fuel.FuelEntry
import com.example.automate.fuel.FuelStorage
import com.example.automate.viewModel.VehicleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Screen for adding a new fuel entry for a specific vehicle.
 *
 * @param vehicleId ID a vehicle to add fuel entry for.
 * @param onSave Called when user clicks on save FAB.
 * @param onCancel Called when user clicks on cancel icon.
 * @param viewModel VehicleViewModel used for data management.
 */
@Composable
fun AddFuelScreen(
    vehicleId: String,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    viewModel: VehicleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

) {
    val context = LocalContext.current
    val vehicle = viewModel.getVehicleById(vehicleId)

    var liters by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var odometer by rememberSaveable { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    var date by rememberSaveable { mutableStateOf(dateFormat.format(calendar.time)) }

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

    // Input validation - odometer value is greater than previous
    val isInputValid = liters.toFloatOrNull()?.let { it > 0 } == true &&
            price.toFloatOrNull()?.let { it > 0 } == true &&
            odometer.toFloatOrNull()?.let { odo ->
                odo > 0 && odo > (vehicle?.currentOdometer?.toFloatOrNull() ?: 0f)
            } == true

    // Error for invalid odometer value
    if (showOdometerError) {
        AlertDialog(
            onDismissRequest = { showOdometerError = false },
            confirmButton = {
                TextButton(onClick = { showOdometerError = false }) {
                    Text(stringResource(R.string.ok))
                }
            },
            title = { Text(stringResource(R.string.invalid_odometer)) },
            text = { Text(stringResource(R.string.new_odometer_value_must_be_greater_than_the_previous_one)) }
        )
    }

    // Layout
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isInputValid) {
                        val entry = FuelEntry(
                            vehicleId = vehicleId,
                            date = date,
                            liters = liters,
                            price = price,
                            odometer = odometer,
                            note = note.takeIf { it.isNotBlank() }
                        )
                        FuelStorage.addEntry(context, entry)

                        // Update odometer of vehicle
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
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top of the screen with title and back icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add_fuel_entry),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Date field
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
                    value = odometer,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) odometer = it },
                    label = { Text(stringResource(R.string.odometer_km)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) price = it },
                    label = { Text(stringResource(R.string.total_price)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = liters,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) liters = it },
                    label = { Text(stringResource(R.string.liters)) },
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
}
