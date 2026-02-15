package com.scanner.wifi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SamplesScreen(samples: List<WifiSample>) {

    Column(Modifier.padding(16.dp)) {

        Text("Samples", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        LazyColumn {

            items(samples) { sample ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Time: ${sample.timestamp}")
                        Text("Lat: ${sample.latitude}")
                        Text("Lon: ${sample.longitude}")
                        Text("SSID: ${sample.ssid}")
                        Text("BSSID: ${sample.bssid}")
                        Text("RSSI: ${sample.rssi}")
                    }
                }
            }
        }
    }
}