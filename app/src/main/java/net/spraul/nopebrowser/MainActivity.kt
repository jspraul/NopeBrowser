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

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()

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
}
