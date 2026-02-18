package com.scanner.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {

    private val PERMISSION_REQUEST_CODE = 100

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.NEARBY_WIFI_DEVICES
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private var samplingJob: Job? = null
    private val samples = mutableListOf<WifiSample>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)

        setContent {
            AppUI(samples)
        }
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        checkPermissions()
    }

    // ---------------- PERMISSIONS ----------------

    private fun checkPermissions() {
        val missing = requiredPermissions.filter { permission ->
            val granted = ContextCompat.checkSelfPermission(this, permission)
            granted != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missing.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            startLocationUpdates()
            startSampling()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startLocationUpdates()
            startSampling()
        }
    }

    // ---------------- LOCATION ----------------

    private fun startLocationUpdates() {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000 // 2 seconds
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                currentLocation = result.lastLocation
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                request,
                callback,
                mainLooper
            )
        }
    }

    // ---------------- SAMPLING LOOP ----------------

    private fun startSampling() {
        if (samplingJob != null) return

        samplingJob = CoroutineScope(Dispatchers.Main).launch {

            val wifiManager =
                applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

            while (isActive) {
                collectSample(wifiManager)
                delay(5000) // every 5 seconds
            }
        }
    }

    private fun stopSampling() {
        samplingJob?.cancel()
        samplingJob = null
    }

    // ---------------- SAMPLE COLLECTION ----------------

    private fun collectSample(wifiManager: WifiManager) {

        val location = currentLocation ?: return

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        wifiManager.startScan()
        val results = wifiManager.scanResults

        for (network in results) {
            samples.add(
                WifiSample(
                    timestamp = System.currentTimeMillis(),
                    ssid = network.SSID,
                    bssid = network.BSSID,
                    rssi = network.level,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    capabilities = network.capabilities
                )
            )
        }

        Toast.makeText(
            this,
            "Samples: ${samples.size}",
            Toast.LENGTH_SHORT
        ).show()
    }
}