import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.automate.R
import com.example.automate.fuel.FuelEntry
import com.example.automate.fuel.FuelStorage
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height

@Composable
fun FuelOverviewScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (FuelEntry) -> Unit
) {
    val context = LocalContext.current
    val entries = remember { FuelStorage.getEntriesForVehicle(context, vehicleId) }

    val sortedEntries = entries.sortedByDescending { it.date }

    val enrichedEntries = sortedEntries.mapIndexed { index, entry ->
        val currentOdo = entry.odometer?.toFloatOrNull() ?: 0f
        val prevOdo = sortedEntries.getOrNull(index + 1)?.odometer?.toFloatOrNull() ?: currentOdo

        val kmDiff = (currentOdo - prevOdo).toInt()
        val liters = entry.liters.toFloatOrNull() ?: 0f
        val price = entry.price.toFloatOrNull() ?: 0f

        val pricePerLiter = if (liters > 0f) price / liters else 0f

        Triple(entry, kmDiff, pricePerLiter)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 40.dp)
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
                    text = "Fuel Overview",
                    style = MaterialTheme.typography.headlineMedium
                )
            }


            LazyColumn(modifier = Modifier.padding(padding)) {
                item{
                    FuelOverallCard(entries = entries)
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .padding(horizontal = 16.dp),
                        thickness = 2.dp
                    )
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                        color = Color.Gray
                    )
                }


                items(enrichedEntries) { (entry, kmDiff, pricePerLiter) ->
                    FuelEntryCard(
                        entry = entry,
                        kmDiff = kmDiff,
                        pricePerLiter = pricePerLiter,
                        modifier = Modifier.clickable {
                            onEditClick(entry)
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun FuelOverallCard(entries: List<FuelEntry>, modifier: Modifier = Modifier) {
    if (entries.isEmpty()) return

    val sortedEntries = entries.sortedBy { it.date }

    val totalKm = sortedEntries
        .zipWithNext { a, b ->
            (b.odometer?.toFloatOrNull() ?: 0f) - (a.odometer?.toFloatOrNull() ?: 0f)
        }
        .filter { it > 0f }
        .sum()

    val totalLiters = sortedEntries.sumOf { it.liters.toFloatOrNull()?.toDouble() ?: 0.0 }
    val totalPrice = sortedEntries.sumOf { it.price.toFloatOrNull()?.toDouble() ?: 0.0 }

    val avgConsumption = if (totalKm > 0) (totalLiters / totalKm) * 100 else 0.0
    val avgPricePerLiter = if (totalLiters > 0) totalPrice / totalLiters else 0.0
    val avgExpenditure = if (entries.isNotEmpty()) totalPrice / entries.size else 0.0
    val avgVolume = if (entries.isNotEmpty()) totalLiters / entries.size else 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Overall",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            RowItem(R.drawable.fuel_pump_icon, "Avg. fuel consumption", String.format("%.1f L/100km", avgConsumption))
            RowItem(R.drawable.fuel_price_icon, "Avg. fuel price", String.format("%.3f €/L", avgPricePerLiter))
            RowItem(R.drawable.money_icon, "Avg. expenditure", String.format("%.0f €", avgExpenditure))
            RowItem(R.drawable.fuel_drop_icon, "Avg. volume", String.format("%.2f L", avgVolume))
        }
    }
}

@Composable
fun RowItem(iconRes: Int, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label)
        }
        Text(text = value)
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun FuelEntryCard(
    entry: FuelEntry,
    kmDiff: Int,
    pricePerLiter: Float,
    modifier: Modifier = Modifier
) {
    val consumption = try {
        val liters = entry.liters.toFloat()
        if (kmDiff > 0) "%.1f L/100km".format((liters / kmDiff) * 100) else ""
    } catch (e: Exception) {
        ""
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.fuel_pump_icon),
                        contentDescription = stringResource(R.string.fuel_pump_icon),
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = entry.date, style = MaterialTheme.typography.bodyLarge)
                        if (consumption.isNotBlank()) {
                            Text(
                                text = consumption,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    entry.odometer?.let {
                        if (it.isNotBlank()) {
                            Text(text = "$it km", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Text(
                        text = "+$kmDiff km",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.money_icon),
                        contentDescription = stringResource(R.string.money_icon),
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(text = "${entry.price} €")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.fuel_price_icon),
                        contentDescription = stringResource(R.string.fuel_price_icon),
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(text = String.format("%.2f €/L", pricePerLiter))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.fuel_drop_icon),
                        contentDescription = stringResource(R.string.fuel_drop_icon),
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(text = "${entry.liters} L")
                }
            }

            entry.note?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
