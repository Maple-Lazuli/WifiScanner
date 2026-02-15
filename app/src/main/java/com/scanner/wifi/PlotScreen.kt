package com.scanner.wifi

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

@Composable
fun PlotScreen(samples: List<WifiSample>) {
    // Better way to hold the reference in Compose
    var webView by remember { mutableStateOf<WebView?>(null) }
    var pageLoaded by remember { mutableStateOf(false) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        Log.d("WEBVIEW", "Page loaded: $url")
                        pageLoaded = true
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        Log.d("WEBVIEW_JS", "${consoleMessage.message()} @ line ${consoleMessage.lineNumber()}")
                        return true
                    }
                }

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    // Allows the local HTML to fetch OpenStreetMap tiles
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                loadUrl("file:///android_asset/leaflet/map.html")
                webView = this
            }
        }
    )

    // Injection Loop
    LaunchedEffect(pageLoaded, samples) {
        if (pageLoaded && samples.isNotEmpty()) {
            val latData = samples.joinToString(",") { it.latitude.toString() }
            val lonData = samples.joinToString(",") { it.longitude.toString() }
            val rssiData = samples.joinToString(",") { it.rssi.toString() }

            // Note: Added an empty array for the 'texts' parameter to prevent JS errors
            val js = "if(window.updatePlot){ window.updatePlot([$latData], [$lonData], [$rssiData], []); }"

            webView?.evaluateJavascript(js, null)
        }
    }
}