package com.example.automate.vehicles

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.rememberAsyncImagePainter
import com.example.automate.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Screen for adding new vehicles.
 *
 * @param onSaveClick Called when user clicks on save button.
 * @param onCancelClick Called when user clicks on cancel icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onSaveClick: (Vehicle) -> Unit,
    onCancelClick: () -> Unit
) {
    var brand by rememberSaveable { mutableStateOf(("")) }
    var model by rememberSaveable { mutableStateOf(("")) }
    var plate by rememberSaveable { mutableStateOf(("")) }
    var vin by rememberSaveable { mutableStateOf(("")) }
    var milage by rememberSaveable { mutableStateOf(("")) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(stringResource(R.string.dd_mm_yyyy), Locale.getDefault())
    var selectedAgeDate by remember { mutableStateOf<String?>(null) }

    var vehicleImage by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        vehicleImage = uri
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            selectedAgeDate = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val enableOptional =
        brand.isNotBlank() || model.isNotBlank() || plate.isNotBlank()

    // Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top of the screen with cancel and save buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onCancelClick() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.cancel)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.create_vehicle),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            val unitOptions = listOf(stringResource(R.string.km), stringResource(R.string.mi))
            val km = stringResource(R.string.km)
            val selectedUnit by remember { mutableStateOf(km) }

            // Save button
            Button(
                onClick = {
                    val imagePath = vehicleImage?.let { uri ->
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(context.contentResolver, uri)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }

                        saveImageToInternalStorage(context, bitmap, "${UUID.randomUUID()}.jpg")
                    }


                    val vehicle = Vehicle(
                        brand = brand,
                        model = model,
                        plate = plate,
                        vin = vin,
                        currentOdometer = milage,
                        unit = selectedUnit,
                        registrationDate = selectedAgeDate,
                        image = imagePath
                    )

                    onSaveClick(vehicle)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(stringResource(R.string.save))
            }
        }

        // Image picker
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (vehicleImage != null) {
                    Image(
                        painter = rememberAsyncImagePainter(vehicleImage),
                        contentDescription = stringResource(R.string.selected_image),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = stringResource(R.string.add_image),
                        modifier = Modifier.size(48.dp),
                        tint = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text(stringResource(R.string.brand)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text(stringResource(R.string.model)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text(stringResource(R.string.license_plate)) },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                thickness = 2.dp
            )

            Text(
                text = stringResource(R.string.optional),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.Gray
            )

            Column {
                OutlinedTextField(
                    value = vin,
                    onValueChange = { vin = it },
                    label = { Text(stringResource(R.string.vin)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enableOptional
                )
            }

            val unitOptions = listOf(stringResource(R.string.km), stringResource(R.string.mi))
            val km = stringResource(R.string.km)
            var selectedUnit by remember { mutableStateOf(km) }
            var expandedUnit by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = !expandedUnit }
                ) {
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = {},
                        readOnly = true,
                        enabled = enableOptional,
                        label = { Text(stringResource(R.string.unit)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                        modifier = Modifier
                            .width(100.dp)
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        unitOptions.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = milage,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            milage = input
                        }
                    },
                    label = { Text(stringResource(R.string.mileage)) },
                    enabled = enableOptional,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Button(
                    onClick = { datePickerDialog.show() },
                    enabled = enableOptional,

                    ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = stringResource(R.string.date),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(stringResource(R.string.add_vehicle_age))
                }


                if (selectedAgeDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.selected_date, selectedAgeDate!!),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Saves image to internal storage.
 *
 * @param context Android context for saving into internal storage
 * @param bitmap Image to be saved.
 * @param filename Filename where image will be stored.
 * @return Path to internal storage.
 */
fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, filename: String): String {
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file.absolutePath
}
