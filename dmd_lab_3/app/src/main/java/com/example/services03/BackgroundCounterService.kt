package com.example.dmd_lab_3

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BackgroundCounterService : Service() {

    private val TAG = "BackgroundCounterService"
    private var isCounting = true
    private var count = 0
    private var countingThread: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startCounting() // Start the counting process
        return START_STICKY // Ensure the service is restarted if killed
    }

    override fun onDestroy() {
        super.onDestroy()
        isCounting = false // Stop counting
        countingThread?.interrupt() // Interrupt the thread if it's running
        countingThread = null
        Log.d(TAG, "Service destroyed")
    }

    private fun startCounting() {
        // Run the counting process in a separate thread
        countingThread = Thread {
            keepCounting()
        }
        countingThread?.start()
    }

    private fun keepCounting() {
        while (isCounting) {
            try {
                count++
                Log.d(TAG, "Count: $count")
                Thread.sleep(1000) // Sleep for 1 second
            } catch (e: InterruptedException) {
                Log.d(TAG, "Counting thread interrupted")
                isCounting = false
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
