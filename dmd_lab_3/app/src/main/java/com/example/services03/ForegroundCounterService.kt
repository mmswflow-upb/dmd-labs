package com.example.dmd_lab_3

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ForegroundCounterService : Service() {

    private val TAG = "ForegroundCounterService"
    private var isCounting = true
    private var count = 0

    private val channelId = "ForegroundCounterServiceChannel"
    private val notificationId = 1
    private lateinit var notificationManager: NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotification("Counter started: $count")
        startForeground(notificationId, notification)

        startCountingThread()

        return START_STICKY // Ensures service is restarted if killed
    }

    override fun onDestroy() {
        super.onDestroy()
        isCounting = false // Stop the counting thread
        Log.d(TAG, "Service destroyed")
    }

    private fun startCountingThread() {
        Thread {
            while (isCounting) {
                count++
                Thread.sleep(1000)
                updateNotification() // Update notification with current count
            }
        }.start()
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            channelId,
            "Counter Service Channel",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Channel for Foreground Counter Service"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(contentText: String): Notification {
        return Notification.Builder(this, channelId)
            .setContentTitle("Foreground Counter Service")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    private fun updateNotification() {
        val updatedNotification = createNotification("Counter value: $count")
        notificationManager.notify(notificationId, updatedNotification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
