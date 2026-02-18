package com.scanner.wifi

data class WifiSample(
    val timestamp: Long,
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val latitude: Double,
    val longitude: Double,
    val capabilities: String?, // Nullable to handle scans where capabilities might be missing
) {
    // This property lives inside the class, so it can see 'capabilities'
    val isSecure: Boolean
        get() {
            val cap = capabilities?.uppercase() ?: ""
            return cap.contains("WPA") ||
                    cap.contains("WEP") ||
                    cap.contains("PSK") ||
                    cap.contains("EAP")
        }
}

fun getAveragedSamples(samples: List<WifiSample>): List<WifiSample> {
    return samples
        .groupBy { it.bssid } // Group by unique hardware ID
        .map { (bssid, readings) ->
            // Use the most recent reading as the 'template' for metadata
            val latest = readings.maxByOrNull { it.timestamp } ?: readings.first()

            WifiSample(
                timestamp = latest.timestamp,
                ssid = latest.ssid,
                bssid = bssid,
                // Average the signal strength
                rssi = readings.map { it.rssi }.average().toInt(),
                // Average the coordinates
                latitude = readings.map { it.latitude }.average(),
                longitude = readings.map { it.longitude }.average(),
                capabilities = latest.capabilities
            )
        }
        .sortedBy { it.ssid } // Optional: put strongest signals at the top
}