package com.scanner.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SamplesScreen(samples: List<WifiSample>) {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Column(Modifier.padding(16.dp)) {
        Text("Samples Table", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        // Header Row
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            TableCell("Time", 100.dp)
            TableCell("Lat", 80.dp)
            TableCell("Lon", 80.dp)
            TableCell("SSID", 120.dp)
            TableCell("BSSID", 140.dp)
            TableCell("RSSI", 60.dp)
        }

        Spacer(Modifier.height(4.dp))

        LazyColumn {
            items(samples) { sample ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    TableCell(dateFormat.format(Date(sample.timestamp)), 100.dp)
                    TableCell(sample.latitude.toString(), 80.dp)
                    TableCell(sample.longitude.toString(), 80.dp)
                    TableCell(sample.ssid, 120.dp)
                    TableCell(sample.bssid, 140.dp)
                    TableCell(sample.rssi.toString(), 60.dp)
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, width: Dp) {
    Text(
        text,
        Modifier
            .width(width)
            .padding(horizontal = 4.dp),
        style = MaterialTheme.typography.bodyMedium
    )
}