package com.scanner.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SamplesScreen(samples: List<WifiSample>) {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Text("Samples Table", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        // Wrap everything in a horizontal scroll so the columns stay "tight"
        // but accessible on narrow screens
        val horizontalScrollState = rememberScrollState()

        Column(Modifier.horizontalScroll(horizontalScrollState)) {
            // Header Row
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(vertical = 4.dp) // Tighter vertical padding
            ) {
                TableCell("Time", 70.dp, isHeader = true)
                TableCell("Lat", 75.dp, isHeader = true)
                TableCell("Lon", 75.dp, isHeader = true)
                TableCell("SSID", 100.dp, isHeader = true)
                TableCell("BSSID", 130.dp, isHeader = true)
                TableCell("RSSI", 45.dp, isHeader = true)
                TableCell("Protected", 100.dp, isHeader = true)
                TableCell("Samples", 50.dp, isHeader = true)

            }

            LazyColumn(Modifier.fillMaxWidth()) {
                items(samples) { sample ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp) // Minimal vertical gap
                    ) {
                        TableCell(dateFormat.format(Date(sample.timestamp)), 70.dp)
                        // Limiting decimal places to keep Lat/Lon tight
                        TableCell("%.5f".format(sample.latitude), 75.dp)
                        TableCell("%.5f".format(sample.longitude), 75.dp)
                        TableCell(sample.ssid, 100.dp)
                        TableCell(sample.bssid, 130.dp)
                        TableCell(sample.rssi.toString(), 45.dp)
                        TableCell(sample.isSecure.toString(), 100.dp)
                        TableCell("${sample.count}", 50.dp)
                    }
                    // Optional: Very thin divider for readability
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    width: Dp,
    isHeader: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        // Use bodySmall for tighter text, and Bold for headers
        style = if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}