package com.example.dmd_lab_4

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val studentViewModel: StudentViewModel by viewModels()

    // Request code for creating a file using SAF
    private val CREATE_FILE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the theme based on SharedPreferences
        val sharedPref = getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("isDarkMode", false)
        Log.d("DARK MODE", isDarkMode.toString())


        setContentView(R.layout.activity_main)

        // Switch for theme toggling
        val themeSwitch: SwitchCompat = findViewById(R.id.theme_switch)
        themeSwitch.isChecked = isDarkMode
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("isDarkMode", isChecked).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )


        }
        // Button to insert some students into the database
        val insertButton: Button = findViewById(R.id.insert_button)
        insertButton.setOnClickListener {
            insertSampleData()
        }

        // Button to save students list sorted alphabetically
        val saveAlphabeticalButton: Button = findViewById(R.id.save_alphabetical_button)
        saveAlphabeticalButton.setOnClickListener {
            writeToInternalStorage()
        }

        // Button to save students list sorted by MeanGrade
        val saveGradeButton: Button = findViewById(R.id.save_grade_button)
        saveGradeButton.setOnClickListener {
            writeToExternalStorage()
        }

        // Display students in the UI
        studentViewModel.getAllStudents().observe(this) { students ->
            val studentTextView: TextView = findViewById(R.id.student_list)
            studentTextView.text =
                students.joinToString("\n") { "${it.name} - ${it.year} - ${it.meanGrade}" }
        }



    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle permissions if necessary
    }

    fun insertSampleData() {
        Thread {
            studentViewModel.deleteAllStudents()
            // Insert random students into the Room database
            val students = listOf(
                Student(name = "Alice", year = 2, meanGrade = Random.nextDouble(1.0, 10.0)),
                Student(name = "Bob", year = 3, meanGrade = Random.nextDouble(1.0, 10.0)),
                Student(name = "Charlie", year = 1, meanGrade = Random.nextDouble(1.0, 10.0))
            )
            Log.d("MainActivity", "Inserting students: $students")  // Debugging log
            studentViewModel.insertStudents(students)
        }.start()
    }

    fun writeToInternalStorage() {
        Thread {
            val students = studentViewModel.getAllStudents().value ?: return@Thread
            Log.d(
                "MainActivity",
                "Sorted students alphabetically: ${students.sortedBy { it.name }}"
            )  // Debugging log
            // Sort alphabetically by student name
            val sortedStudents = students.sortedBy { it.name }
            val file = File(filesDir, "students_sorted_alphabetically.txt")

            // Delete the file if it already exists
            if (file.exists()) {
                file.delete()
            }

            try {
                FileOutputStream(file).use {
                    it.write(sortedStudents.joinToString("\n") { "${it.name} - ${it.year} - ${it.meanGrade}" }
                        .toByteArray())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun writeToExternalStorage() {
        // Get the list of students
        val students = studentViewModel.getAllStudents().value ?: return
        // Sort by mean grade (descending order)
        val sortedStudents = students.sortedByDescending { it.meanGrade }

        // Create an Intent to let the user pick where to save the file
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Set the MIME type to plain text
            type = "text/plain"
            // Set the default file name (user can change this)
            putExtra(Intent.EXTRA_TITLE, "students_sorted_by_grade.txt")
        }

        // Start the activity for result to let the user pick a location
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the URI of the selected location
            val uri = data?.data ?: return

            // Now, we will write the data to the selected location
            writeToUri(uri, studentViewModel.getAllStudents().value ?: emptyList())
        }
    }

    private fun writeToUri(uri: Uri, students: List<Student>) {
        val sortedStudents = students.sortedByDescending { it.meanGrade }

        try {
            // Open an output stream to the selected URI
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                // Convert the list of students to a string and write it to the output stream
                val data =
                    sortedStudents.joinToString("\n") { "${it.name} - ${it.year} - ${it.meanGrade}" }
                outputStream.write(data.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any errors that occur while writing
        }
    }
}
