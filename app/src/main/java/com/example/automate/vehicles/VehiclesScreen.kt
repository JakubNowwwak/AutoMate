package com.example.automate.vehicles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.automate.R

/**
 * Main screen which shows a list of vehicles added by an user.
 *
 * @param vehicles List of vehicles.
 * @param onAddVehicleClick Called, when user clicks on FAB.
 * @param onVehicleClick Called, when user clicks on a vehicle from lazy column.
 */
@Composable
fun VehiclesScreen(
    vehicles: List<Vehicle>,
    onAddVehicleClick: () -> Unit = {},
    onVehicleClick: (String) -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            // FAB for adding a new vehicle
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.add_vehicle)) },
                icon = { Icon(Icons.Filled.Create, contentDescription = null) },
                onClick = onAddVehicleClick,
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Creates "AUTOMATE" title
            Box(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.auto))
                        addStyle(
                            style = SpanStyle(fontWeight = FontWeight.W900),
                            start = 0,
                            end = 4
                        )
                        append(stringResource(R.string.mate))
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = MaterialTheme.typography.displaySmall.fontSize
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                thickness = 2.dp
            )

            // Text shows after users adds vehicles
            if (vehicles.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.select_your_vehicle),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }


            Spacer(modifier = Modifier.height(4.dp))

            // Lazy column which shows all vehicles
            LazyColumn {
                items(vehicles) { vehicle ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = { onVehicleClick(vehicle.id) }

                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        {
                            // Image of a vehicle
                            if (!vehicle.image.isNullOrBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(vehicle.image),
                                    contentDescription = stringResource(R.string.vehicle_image),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Gradient
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            ),
                                            startY = 100f
                                        )
                                    )
                            )

                            // Vehicle info
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = vehicle.plate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                                Text(
                                    text = "${vehicle.brand} ${vehicle.model}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
