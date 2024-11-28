package com.example.dmd_lab_2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dmd_lab_2.databinding.ActivityMainBinding
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Logs for the onCreate method
        Log.v("TestLogs", "onCreate has been called (Verbose log)")
        Log.d("TestLogs", "onCreate Debug log")
        Log.i("TestLogs", "onCreate Info log")
        Log.w("TestLogs", "onCreate Warning log")
        Log.e("TestLogs", "onCreate Error log")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Set click listener for the Calculate button
        binding.btnCalculate.setOnClickListener {
            logUserInput() // Log the user input data
            calculateBMI()  // Compute and display the BMI
        }
    }

    // Log user input data
    private fun logUserInput() {
        val heightStr = binding.etHeight.text.toString()
        val weightStr = binding.etWeight.text.toString()

        Log.d("TestLogs", "User entered height: $heightStr cm")
        Log.d("TestLogs", "User entered weight: $weightStr kg")
    }

    private fun calculateBMI() {
        try {
            val heightStr = binding.etHeight.text.toString()
            val weightStr = binding.etWeight.text.toString()

            if (heightStr.isNotEmpty() && weightStr.isNotEmpty()) {
                val height = heightStr.toDouble() / 100
                val weight = weightStr.toDouble()

                if (height > 0 && weight > 0) {
                    val bmi = weight / (height * height)

                    // Start SecondActivity and pass the BMI value
                    val intent = Intent(this, SecondActivity::class.java)
                    intent.putExtra("BMI", bmi)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Invalid height or weight", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("TestLogs", "Error in BMI calculation", e)
            Toast.makeText(this, "An error occurred. Please check your input.", Toast.LENGTH_SHORT).show()
        }
    }



    // Lifecycle methods with logs
    override fun onStart() {
        super.onStart()
        Log.i("TestLogs", "onStart has been called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("TestLogs", "onResume has been called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("TestLogs", "onPause has been called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("TestLogs", "onStop has been called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("TestLogs", "onDestroy has been called")
    }
}
