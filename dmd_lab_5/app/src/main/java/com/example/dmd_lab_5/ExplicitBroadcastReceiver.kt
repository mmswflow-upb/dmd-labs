package com.example.dmd_lab_5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ExplicitBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("custom_message")
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}
