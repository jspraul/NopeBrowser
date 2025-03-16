package net.spraul.nopebrowser

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import net.spraul.nopebrowser.databinding.ActivityMainBinding
import java.io.ByteArrayInputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.fixedRateTimer

private const val TAG = "NopeBrowser"
private val denyUrlContains = arrayOf(
    "youtube.com/watch"
)
private val allowUrlContains = arrayOf(
    "www.google-analytics.com", "www.google.com",
    ".abeka.com",
    "goodandbeautiful.com",
    ".vimeocdn.com", "player.vimeo.com", "player-telemetry.vimeo.com", ".cloudfront.net", ".akamaized.net",
    "www.youtube.com", ".googlevideo.com", ".gstatic.com"
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val started: Long = System.nanoTime()
    private var mostRecentRequest: Long = started

    private val broadcastSocket = DatagramSocket().also{ it.broadcast = true }
    private val broadcastIp = InetAddress.getByAddress(byteArrayOf(192.toByte(), 168.toByte(), 1, 255.toByte()))
    private val heartbeatData = "NopeBrowser:${started}:FOREGROUND".toByteArray()
    private val heartbeatPacket = DatagramPacket(heartbeatData, heartbeatData.size, broadcastIp, 2119)
    private val reqData = "NopeBrowser:${started}:REQ".toByteArray()
    private val reqPacket = DatagramPacket(reqData, reqData.size, broadcastIp, 2119)
    private fun broadcast(packet: DatagramPacket, desc: String) {
        try {
            broadcastSocket.send(packet)
            Log.e(TAG, "BROADCAST: $desc")
        }
        catch(exc: Exception){
            Log.e(TAG, "UDP send failed ($desc)", exc)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lifecycleObserver = object : DefaultLifecycleObserver {
            var timer: Timer? = null
            var isTimerRunning: Boolean = false
            override fun onStart(owner: LifecycleOwner){
                timer?.cancel()
                timer = fixedRateTimer(period = 2500L) {
                    val now = System.nanoTime()
                    if (now - mostRecentRequest > 5L * 60L * 1_000_000_000L) {
                        Log.e(TAG, "heartbeat stopped ${now}-${mostRecentRequest}=${now-mostRecentRequest}")
                        onStop(owner);
                    }
                    broadcast(heartbeatPacket, "heartbeat")
                }
            }
            override fun onStop(owner: LifecycleOwner) {
                timer?.cancel()
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)

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
                val url = request.url.toString()
                return if (allowUrl(url)) {
                    Log.i(TAG, "YUP URL $url")
                    super.shouldOverrideUrlLoading(view, request)
                } else {
                    Log.i(TAG, "NOPE URL $url")
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
                    if (System.nanoTime() - mostRecentRequest > 1_000_000_000L) {
                        broadcast(reqPacket, "REQ")
                    }
                    mostRecentRequest = System.nanoTime()
                    Log.i(TAG, "YUP REQ $url")
                    super.shouldInterceptRequest(view, request)
                } else {
                    if (denyHostCount.containsKey(host)) {
                        denyHostCount[host] = denyHostCount.getValue(host) + 1
                    }
                    else {
                        denyHostCount[host] = 1
                    }
                    Log.i(TAG, "NOPE REQ $url")
                    WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
                }
            }

            // TODO: call on cleanup (WebView.onDetachedFromWindow?)
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

        binding.buttonAbeka.setOnClickListener {
            binding.webView.loadUrl("https://athome.abeka.com/Video2/Streaming/Default.aspx")
        }
        binding.buttonHistory.setOnClickListener {
            binding.webView.loadUrl("https://goodandbeautiful.com/history3/")
        }
        binding.buttonMath.setOnClickListener {
            binding.webView.loadUrl("https://goodandbeautiful.com/math4/")
        }
        binding.buttonScience.setOnClickListener {
            binding.webView.loadUrl("https://goodandbeautiful.com/sciencevideos/")
        }
        binding.buttonDebug.setOnClickListener {
            filter.logAndClearHostCount()
        }

        binding.viewFinder.visibility = View.GONE;
        /*
        fun startCamera(){
            val camera = ProcessCameraProvider.getInstance(this);
            camera.addListener( {
                val cameraProvider = camera.get();
                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA,
                        Preview.Builder().build().also {
                            it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                        })
                }
                catch(exc: Exception){
                    Log.e(TAG, "Camera binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(this))
        }

        if (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val requestPermissionActivityLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    if (it.key == Manifest.permission.CAMERA && !it.value) {
                        Toast.makeText(baseContext, "Camera permission denied", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        startCamera();
                    }
                }
            }
            requestPermissionActivityLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
        else {
            startCamera();
        }
        */
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
