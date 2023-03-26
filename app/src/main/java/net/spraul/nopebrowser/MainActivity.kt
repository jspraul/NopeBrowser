package net.spraul.nopebrowser

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the WebView
        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true

        // Set up the button click listeners
        button1.setOnClickListener { loadWebsite("https://www.google.com") }
        button2.setOnClickListener { loadWebsite("https://www.youtube.com") }
        button3.setOnClickListener { loadWebsite("https://www.reddit.com") }
    }

    // Load the specified URL in the WebView
    private fun loadWebsite(url: String) {
        webview.loadUrl(url)
    }
}

