package com.example.automate.vehicles

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VehiclesScreen(
    onAddVehicleClick: () -> Unit = {},
    onVehicleClick: (String) -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Vehicle") },
                icon = { Icon(Icons.Filled.Create, contentDescription = null) },
                onClick = onAddVehicleClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                IconButton(
                    onClick = { /* TODO: menu  */ },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu"
                    )
                }

                Text(
                    text = buildAnnotatedString {
                        append("AUTO")
                        addStyle(
                            style = SpanStyle(fontWeight = FontWeight.W900),
                            start = 0,
                            end = 4
                        )
                        append("MATE")
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
                thickness = 1.dp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Select your vehicle",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn {
                items(5) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = { onVehicleClick("vehicleId-$index") }

                    ) {
                        Text(
                            text = "Vehicle $index",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
