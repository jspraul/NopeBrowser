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

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                return if (allowUrl(url)) {
                    Log.i(TAG, "Allowing URL: $url")
                    super.shouldOverrideUrlLoading(view, request)
                } else {
                    Log.i(TAG, "Blocking URL: $url")
                    true
                }
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url.toString()
                return if (allowUrl(url)) {
                    Log.i(TAG, "Allowed content: $url")
                    super.shouldInterceptRequest(view, request)
                } else {
                    Log.i(TAG, "Blocking content: $url")
                    WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
                }
            }
        }

        binding.buttonGoodAndBeautiful.setOnClickListener {
            binding.webView.loadUrl("https://goodandbeautiful.com")
        }
        binding.buttonAbeka.setOnClickListener {
            binding.webView.loadUrl("https://athome.abeka.com/Video2/Streaming/Default.aspx")
        }
        binding.buttonTriumphBaptist.setOnClickListener {
            binding.webView.loadUrl("https://triumphbaptist.org")
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
