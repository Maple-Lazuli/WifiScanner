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
    var webView by remember { mutableStateOf<WebView?>(null) }
    var pageLoaded by remember { mutableStateOf(false) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
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
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                loadUrl("file:///android_asset/leaflet/map.html")
                webView = this
            }
        }
    )

    LaunchedEffect(pageLoaded, samples) {
        if (pageLoaded && samples.isNotEmpty()) {
            val latData = samples.joinToString(",") { it.latitude.toString() }
            val lonData = samples.joinToString(",") { it.longitude.toString() }
            val rssiData = samples.joinToString(",") { it.rssi.toString() }

            // Logic: Check capabilities for security keywords.
            // Returns true if secure, false if open.
            val protectionData = samples.joinToString(",") { it.isSecure.toString() }

            val js = "if(window.updatePlot){ window.updatePlot([$latData], [$lonData], [$rssiData], [$protectionData]); }"
            webView?.evaluateJavascript(js, null)
        }
    }
}