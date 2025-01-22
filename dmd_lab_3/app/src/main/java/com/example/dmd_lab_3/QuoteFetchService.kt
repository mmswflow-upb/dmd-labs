package com.example.dmd_lab_3

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class QuoteFetchService : Service() {

    private val binder = QuoteBinder()
    private var latestQuote: String = "Fetching quote..."
    private var latestAuthor: String = ""
    private var isFetching = false
    private var fetchThread: Thread? = null

    override fun onBind(intent: Intent?): IBinder {
        startFetching() // Start fetching when the service is bound
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopFetching() // Stop fetching when all clients unbind
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopFetching() // Ensure fetching stops when the service is destroyed
    }

    private fun startFetching() {
        if (isFetching) return // Avoid starting multiple threads

        isFetching = true
        fetchThread = Thread {
            while (isFetching) {
                try {
                    fetchQuote()
                    Thread.sleep(1000) // Fetch every second
                } catch (e: InterruptedException) {
                    Log.e("QuoteFetchService", "Fetching thread interrupted: ${e.message}")
                    isFetching = false
                }
            }
        }
        fetchThread?.start()
    }

    private fun stopFetching() {
        isFetching = false
        fetchThread?.interrupt() // Stop the thread gracefully
        fetchThread = null
    }

    private fun fetchQuote() {
        try {
            val url = URL("https://quotes-api-self.vercel.app/quote")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                latestQuote = json.getString("quote")
                latestAuthor = json.getString("author")
            } else {
                Log.e("QuoteFetchService", "Error fetching quote: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            Log.e("QuoteFetchService", "Exception: ${e.message}")
        }
    }

    fun getLatestQuote(): Pair<String, String> {
        return Pair(latestQuote, latestAuthor)
    }

    inner class QuoteBinder : Binder() {
        fun getService(): QuoteFetchService {
            return this@QuoteFetchService
        }
    }
}