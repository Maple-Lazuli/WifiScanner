package com.scanner.wifi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*

@Composable
fun AppUI(samples: List<WifiSample>) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("settings") },
                    label = { Text("Settings") },
                    icon = {}
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("samples") },
                    label = { Text("Samples") },
                    icon = {}
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("plot") },
                    label = { Text("Plot") },
                    icon = {}
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "samples",
            modifier = Modifier.padding(padding)
        ) {

            composable("settings") { SettingsScreen() }
            composable("samples") { SamplesScreen(samples) }
            composable("plot") { PlotScreen() }
        }
    }
}