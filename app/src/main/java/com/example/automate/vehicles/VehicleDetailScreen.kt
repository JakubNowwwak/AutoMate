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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.automate.R
import com.example.automate.navigation.Routes

/**
 * Screen that shows actions which can be performed on a vehicle.
 *
 * @param vehicle Vehicle instance.
 * @param onBackClick Called when user clicks on back button.
 * @param onDeleteClick Called when user clicks on remove vehicle button.
 * @param navController Navigation controller for navigation to other screens.
 */
@Composable
fun VehicleDetailScreen(
    vehicle: Vehicle,
    onBackClick: () -> Unit,
    onDeleteClick: (Vehicle) -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // Top of the screen with title and back buttons
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
                    text = stringResource(R.string.vehicle_overview),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        // Image and title of the vehicle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(vehicle.image),
                contentDescription = stringResource(R.string.vehicle_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Text(
                text = "${vehicle.brand} ${vehicle.model}",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }

        // Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { navController.navigate("${Routes.MODIFY_VEHICLE}/${vehicle.id}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(stringResource(R.string.modify_information))
            }

            Button(
                onClick = {
                    navController.navigate("fuel_overview/${vehicle.id}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(stringResource(R.string.fuel_overview))
            }


            Button(
                onClick = {
                    navController.navigate("maintenance_overview/${vehicle.id}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(stringResource(R.string.maintenance))
            }

            // Delete and confirmation dialog
            var showDialog by remember { mutableStateOf(false) }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC91818))
            ) {
                Text(stringResource(R.string.remove_vehicle))
            }


            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.confirmation)) },
                    text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_the_vehicle)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                onDeleteClick(vehicle)
                                showDialog = false
                            }
                        ) {
                            Text(stringResource(R.string.yes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}