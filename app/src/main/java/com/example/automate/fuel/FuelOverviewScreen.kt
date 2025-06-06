package com.example.automate.fuel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.automate.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FuelOverviewScreen(
    vehicleId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var entries by remember { mutableStateOf(FuelStorage.getEntriesForVehicle(context, vehicleId)) }

    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.fuel_overview),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn {
                items(entries) { entry: FuelEntry ->
                    Text(text = "Date: ${entry.date}")
                    Text(text = "Liters: ${entry.liters}, Price: ${entry.price}")
                    if (!entry.note.isNullOrBlank()) {
                        Text(text = "Note: ${entry.note}")
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newEntry = FuelEntry(
                        vehicleId = vehicleId,
                        date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()),
                        liters = "35.0",
                        price = "50.0"
                    )
                    FuelStorage.addEntry(context, newEntry)
                    entries = FuelStorage.getEntriesForVehicle(context, vehicleId)
                }
            ) {
                Text("Add Entry")
            }
        }
    }
}
