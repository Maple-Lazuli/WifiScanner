package com.scanner.wifi

data class WifiSample(
    val timestamp: Long,
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val latitude: Double,
    val longitude: Double,
    val capabilities: String?, // Nullable to handle scans where capabilities might be missing\
    val count: Int =  1
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

fun getWeightedAveragedSamples(samples: List<WifiSample>): List<WifiSample> {
    return samples
        .groupBy { it.bssid }
        .map { (bssid, readings) ->
            val latest = readings.maxByOrNull { it.timestamp } ?: readings.first()

            var totalWeight = 0.0
            var weightedLat = 0.0
            var weightedLon = 0.0

            readings.forEach { sample ->
                // Convert RSSI to a positive weight (0 to 70 range)
                // -100 becomes 1, -30 becomes 71.
                // We square it to give even more priority to strong signals.
                val weight = Math.pow((sample.rssi + 101).toDouble(), 2.0)

                weightedLat += sample.latitude * weight
                weightedLon += sample.longitude * weight
                totalWeight += weight
            }

            WifiSample(
                timestamp = latest.timestamp,
                ssid = latest.ssid,
                bssid = bssid,
                rssi = readings.map { it.rssi }.average().toInt(),
                latitude = weightedLat / totalWeight,
                longitude = weightedLon / totalWeight,
                capabilities = latest.capabilities,
                count = readings.size // Store how many pings we found
            )
        }
        .sortedBy { it.ssid }
}