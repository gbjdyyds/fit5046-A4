package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddClothViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddClothViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddClothViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
