package com.scanner.wifi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {

    var sampleRate by remember { mutableStateOf("5000") }
    var writeToDisk by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {

        Text("App Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = sampleRate,
            onValueChange = { sampleRate = it },
            label = { Text("Sample Rate (ms)") }
        )

        Spacer(Modifier.height(16.dp))

        Row {
            Checkbox(
                checked = writeToDisk,
                onCheckedChange = { writeToDisk = it }
            )
            Text("Write samples to disk")
        }
    }
}