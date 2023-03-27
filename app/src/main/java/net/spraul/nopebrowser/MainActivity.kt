package net.spraul.nopebrowser

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import net.spraul.nopebrowser.databinding.ActivityMainBinding
import java.io.ByteArrayInputStream

private const val TAG = "NopeBrowser"
private val denyUrlContains = arrayOf(
    "youtube.com/watch"
)
private val allowUrlContains = arrayOf(
    "192.168.1.1",
    "polyfill.io", "www.google-analytics.com", "www.google.com",
    "abcmouse",
    "abeka",
      //"js.monitor.azure.com", "applicationinsights.azure.com",
      //"static.hotjar.com", "script.hotjar.com",
      //"apps.usw2.pure.cloud",
    "babbel", "cloudfront",
    "goodandbeautiful", "scratch", "vimeocdn", "player.vimeo.com",
    "youtube", "googlevideo", "gstatic"
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.allowFileAccess = false
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        val filter = object : WebViewClient() {
            var denyHostCount = HashMap<String, Int>()
            var allowHostCount = HashMap<String, Int>()

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val host = request.url?.host ?: ""
                val url = request.url.toString()
                return if (allowUrl(url)) {
                    Log.i(TAG, "YUP URL $host $url")
                    super.shouldOverrideUrlLoading(view, request)
                } else {
                    Log.i(TAG, "NOPE URL $host $url")
                    true
                }
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val host = request?.url?.host ?: ""
                val url = request?.url.toString()
                return if (allowUrl(url)) {
                    if (allowHostCount.containsKey(host)) {
                        allowHostCount[host] = allowHostCount.getValue(host) + 1
                    }
                    else {
                        allowHostCount[host] = 1
                    }
                    Log.i(TAG, "YUP REQ $host $url")
                    super.shouldInterceptRequest(view, request)
                } else {
                    if (denyHostCount.containsKey(host)) {
                        denyHostCount[host] = denyHostCount.getValue(host) + 1
                    }
                    else {
                        denyHostCount[host] = 1
                    }
                    Log.i(TAG, "NOPE REQ $host $url")
                    WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
                }
            }

            fun logAndClearHostCount()
            {
                var sortedEntries = allowHostCount.entries.sortedByDescending { it.value }
                var logMessage = StringBuilder("YUP REQ hosts\n")
                for ((host, count) in sortedEntries) {
                    logMessage.append("\t%4d $host\n".format(count))
                }
                Log.i(TAG, logMessage.toString())
                allowHostCount.clear()

                sortedEntries = denyHostCount.entries.sortedByDescending { it.value }
                logMessage = StringBuilder("NOPE REQ hosts\n")
                for ((host, count) in sortedEntries) {
                    logMessage.append("\t%4d $host\n".format(count))
                }
                Log.i(TAG, logMessage.toString())
                denyHostCount.clear()
            }
        }
        binding.webView.webViewClient = filter

        binding.buttonGoodAndBeautiful.setOnClickListener {
            binding.webView.loadUrl("https://goodandbeautiful.com")
        }
        binding.buttonAbeka.setOnClickListener {
            binding.webView.loadUrl("https://athome.abeka.com/Video2/Streaming/Default.aspx")
        }
        binding.buttonTriumphBaptist.setOnClickListener {
            binding.webView.loadUrl("https://triumphbaptist.org")
        }
        binding.buttonDebug.setOnClickListener {
            filter.logAndClearHostCount()
        }
    }

    // TODO: https://stackoverflow.com/questions/72634225/onbackpressed-deprecated-what-is-the-alternative
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        else
            super.onBackPressed()
    }

    fun allowUrl(url: String): Boolean {
        for (str in denyUrlContains) {
            if (url.contains(str))
                return false
        }
        for (str in allowUrlContains) {
            if (url.contains(str)) {
                return true
            }
        }
        return false
    }
}
