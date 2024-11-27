package com.example.dmd_lab_3

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.dmd_lab_3.databinding.ActivityMainBinding
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isBound = false
    private var quoteService: QuoteFetchService? = null
    private var foregroundServiceRunning = false
    private var backgroundServiceRunning = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Notification permission is required for foreground service", Toast.LENGTH_SHORT).show()
            }
        }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val quoteBinder = binder as QuoteFetchService.QuoteBinder
            quoteService = quoteBinder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            quoteService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Foreground Service
        binding.startForegroundServiceButton.setOnClickListener {
            if (!foregroundServiceRunning) {
                val intent = Intent(this, ForegroundCounterService::class.java)
                startForegroundService(intent)
                foregroundServiceRunning = true
            } else {
                Toast.makeText(this, "Foreground service is already running", Toast.LENGTH_SHORT).show()
            }
        }

        binding.stopForegroundServiceButton.setOnClickListener {
            if (foregroundServiceRunning) {
                val intent = Intent(this, ForegroundCounterService::class.java)
                stopService(intent)
                foregroundServiceRunning = false
            } else {
                Toast.makeText(this, "Foreground service is not running", Toast.LENGTH_SHORT).show()
            }
        }

        // Background Service
        binding.startBackgroundServiceButton.setOnClickListener {
            if (!backgroundServiceRunning) {
                val intent = Intent(this, BackgroundCounterService::class.java)
                startService(intent)
                backgroundServiceRunning = true
            } else {
                Toast.makeText(this, "Background service is already running", Toast.LENGTH_SHORT).show()
            }
        }

        binding.stopBackgroundServiceButton.setOnClickListener {
            if (backgroundServiceRunning) {
                val intent = Intent(this, BackgroundCounterService::class.java)
                stopService(intent)
                backgroundServiceRunning = false
            } else {
                Toast.makeText(this, "Background service is not running", Toast.LENGTH_SHORT).show()
            }
        }

        // Bound Service
        binding.bindServiceButton.setOnClickListener {
            if (!isBound) {
                val intent = Intent(this, QuoteFetchService::class.java)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            } else {
                Toast.makeText(this, "Service is already bound", Toast.LENGTH_SHORT).show()
            }
        }

        binding.unbindServiceButton.setOnClickListener {
            if (isBound) {
                unbindService(serviceConnection)
                isBound = false
            } else {
                Toast.makeText(this, "Service is not bound", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fetchQuoteButton.setOnClickListener {
            if (isBound && quoteService != null) {
                val (quote, author) = quoteService!!.getLatestQuote()
                binding.quoteTextView.text = "\"$quote\"\n\n- $author"
            } else {
                Toast.makeText(this, "Service is not bound", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
