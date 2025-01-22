package com.example.dmd_lab_5

import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {


    private lateinit var editText: EditText
    private lateinit var sendHashButton: Button

    // Dynamic receiver for Airplane Mode
    private val airplaneModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                val isAirplaneModeOn = Settings.Global.getInt(
                    contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0
                ) != 0
                Log.d("AirplaneMode", "Airplane Mode State: ${isAirplaneModeOn}")
            }
        }
    }

    // Dynamic receiver for receiving hashed message
    private val hashedMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val hashedMessage = intent?.getStringExtra("hashed_message")
            hashedMessage?.let {
                Toast.makeText(context, "Hashed Message: $it", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editTextMessage)
        sendHashButton = findViewById(R.id.buttonSendHash)

        // Register dynamic receiver for Airplane Mode
        val airplaneModeFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(airplaneModeReceiver, airplaneModeFilter)

        // Register dynamic receiver for hashed messages
        val hashedMessageFilter = IntentFilter("com.example.dmd_lab_5.HASHED_MESSAGE")
        registerReceiver(hashedMessageReceiver, hashedMessageFilter, RECEIVER_NOT_EXPORTED)

        // Send Hash Button
        sendHashButton.setOnClickListener {
            val inputMessage = editText.text.toString()
            if (inputMessage.isNotEmpty()) {
                // Hashing the message in a background thread
                thread {
                    val hashedMessage = hashMessage(inputMessage)

                    // Sending an implicit broadcast with hashed message
                    val intent = Intent("com.example.dmd_lab_5.HASHED_MESSAGE")
                    intent.setPackage(packageName) // Restrict the intent to this app
                    intent.putExtra("hashed_message", hashedMessage)
                    sendBroadcast(intent)
                }
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        // Send an explicit broadcast
        val explicitIntent = Intent(this, ExplicitBroadcastReceiver::class.java)
        explicitIntent.putExtra("custom_message", "Hello from explicit broadcast!")
        sendBroadcast(explicitIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(airplaneModeReceiver)
        unregisterReceiver(hashedMessageReceiver)
    }

    // Function to hash a message using SHA-256
    private fun hashMessage(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
