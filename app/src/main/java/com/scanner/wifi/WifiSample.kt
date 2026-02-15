package com.scanner.wifi

data class WifiSample(
    val timestamp: Long,
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val latitude: Double,
    val longitude: Double
)