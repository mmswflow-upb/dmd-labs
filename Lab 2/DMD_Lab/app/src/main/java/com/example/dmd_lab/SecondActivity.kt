package com.example.dmd_lab

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dmd_lab.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the BMI from the Intent
        val bmi = intent.getDoubleExtra("BMI", 0.0)
        binding.tvBmiResult.text = "B.M.I. is: %.2f".format(bmi)

        // Share button to share BMI result
        binding.btnShare.setOnClickListener {
            shareBMI(bmi)
        }

        // Info button to open BMI info webpage
        binding.btnInfo.setOnClickListener {
            openBMIInfoPage()
        }
    }

    private fun shareBMI(bmi: Double) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "My B.M.I. is $bmi.")
        }
        startActivity(Intent.createChooser(shareIntent, "Share BMI"))
    }

    private fun openBMIInfoPage() {
        val url = "https://ro.wikipedia.org/wiki/Indice_de_mas%C4%83_corporal%C4%83" // Replace with actual BMI info URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
