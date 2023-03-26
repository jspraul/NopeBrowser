package net.spraul.nopebrowser

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import net.spraul.nopebrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.webViewClient = WebViewClient()

        binding.buttonGoogle.setOnClickListener {
            binding.webView.loadUrl("https://www.google.com")
        }

        binding.buttonFacebook.setOnClickListener {
            binding.webView.loadUrl("https://www.facebook.com")
        }

        binding.buttonTwitter.setOnClickListener {
            binding.webView.loadUrl("https://www.twitter.com")
        }
    }
}
