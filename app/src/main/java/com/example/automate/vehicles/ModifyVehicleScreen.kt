package com.example.automate.vehicles

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.automate.R
import com.example.automate.model.Vehicle
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyVehicleScreen(
    vehicleToEdit: Vehicle,
    onSaveClick: (Vehicle) -> Unit,
    onCancelClick: () -> Unit
) {
    val textFieldValueSaver = listSaver<TextFieldValue, Any>(
        save = { listOf(it.text) },
        restore = { TextFieldValue(it[0] as String) }
    )

    var brand by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(vehicleToEdit.brand)) }
    var model by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(vehicleToEdit.model)) }
    var plate by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(vehicleToEdit.plate)) }
    var vin by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(vehicleToEdit.vin ?: "")) }
    var currentOdometer by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(vehicleToEdit.currentOdometer ?: "")) }
    var selectedUnit by rememberSaveable { mutableStateOf(vehicleToEdit.unit ?: "km") }
    var selectedAgeDate by rememberSaveable { mutableStateOf(vehicleToEdit.registrationDate) }
    var vehicleImage by rememberSaveable { mutableStateOf(vehicleToEdit.image?.let { Uri.parse(it) }) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

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
        brand.text.isNotBlank() || model.text.isNotBlank() || plate.text.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancelClick) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.cancel)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Vehicle",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Button(
                onClick = {
                    val imagePath = if (vehicleImage != null && vehicleImage.toString().startsWith("content://")) {
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(context.contentResolver, vehicleImage!!)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, vehicleImage!!)
                        }
                        saveImageToInternalStorage(context, bitmap, "${UUID.randomUUID()}.jpg")
                    } else {
                        vehicleToEdit.image
                    }

                    val updatedVehicle = Vehicle(
                        id = vehicleToEdit.id,
                        brand = brand.text,
                        model = model.text,
                        plate = plate.text,
                        vin = vin.text,
                        currentOdometer = currentOdometer.text,
                        unit = selectedUnit,
                        registrationDate = selectedAgeDate,
                        image = imagePath
                    )
                    onSaveClick(updatedVehicle)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(stringResource(R.string.save))
            }
        }

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

            OutlinedTextField(
                value = vin,
                onValueChange = { vin = it },
                label = { Text(stringResource(R.string.vin)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = enableOptional
            )

            val unitOptions = listOf(stringResource(R.string.km), stringResource(R.string.mi))
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
                        modifier = Modifier.width(100.dp).menuAnchor()
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
                    value = currentOdometer,
                    onValueChange = { currentOdometer = it },
                    label = { Text(stringResource(R.string.mileage)) },
                    enabled = enableOptional,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = { datePickerDialog.show() },
                enabled = enableOptional
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