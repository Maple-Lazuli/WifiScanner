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