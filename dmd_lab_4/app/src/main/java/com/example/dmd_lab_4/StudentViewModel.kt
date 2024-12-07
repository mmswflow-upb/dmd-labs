package com.example.dmd_lab_4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val studentDao = StudentDatabase.getDatabase(application).studentDao()
    private val allStudents: LiveData<List<Student>> = studentDao.getAllStudents()

    fun getAllStudents(): LiveData<List<Student>> {
        return allStudents
    }

    fun insertStudents(students: List<Student>) {
        viewModelScope.launch {
            studentDao.insertStudents(students)
        }
    }
    // In your StudentViewModel
    fun deleteAllStudents() {
        viewModelScope.launch {
            studentDao.deleteAll()
        }
    }

}
