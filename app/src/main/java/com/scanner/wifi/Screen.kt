package com.scanner.wifi

sealed class Screen(val route: String) {
    object Settings : Screen("settings")
    object Samples : Screen("samples")
    object Plot : Screen("plot")
}