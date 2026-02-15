package com.scanner.wifi

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

@Composable
fun PlotScreen(samples: List<WifiSample>) {

    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val initialHtml = """
    <html>
      <head>
        <script src="file:///android_asset/plotly/plotly.min.js"></script>
      </head>
      <body>
        <div id="plot" style="width:100%;height:100%;"></div>
        <script>
          window.onload = function() {
            var trace = {
                x: [],
                y: [],
                mode: 'markers',
                marker: {size:10, color:[], colorscale:'Viridis', showscale:true},
                text: []
            };
            var data = [trace];
            var layout = {
                title: 'Live WiFi Samples',
                xaxis: {title:'Longitude'},
                yaxis: {title:'Latitude'}
            };
            Plotly.newPlot('plot', data, layout);

            // expose function to update plot
            window.updatePlot = function(xs, ys, colors, texts) {
                Plotly.update('plot', {
                    x: [xs],
                    y: [ys],
                    'marker.color': [colors],
                    text: [texts]
                });
            }
          }
        </script>
      </body>
    </html>
    """.trimIndent()

    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadDataWithBaseURL(
                "file:///android_asset/", initialHtml,
                "text/html", "UTF-8", null
            )
            webViewRef = this
        }
    }, modifier = Modifier.fillMaxSize())

    // live update loop
    LaunchedEffect(samples) {
        while (true) {
            val xData = samples.joinToString(",") { it.longitude.toString() }
            val yData = samples.joinToString(",") { it.latitude.toString() }
            val rssiData = samples.joinToString(",") { it.rssi.toString() }
            val textData = samples.joinToString(",") { "'${it.ssid} ${it.rssi}'" }

            val js = """
                if(window.updatePlot) {
                    window.updatePlot([$xData], [$yData], [$rssiData], [$textData]);
                }
            """.trimIndent()

            webViewRef?.post {
                webViewRef?.evaluateJavascript(js, null)
            }

            delay(2000)
        }
    }
}