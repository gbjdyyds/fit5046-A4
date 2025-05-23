package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClothDetailViewModelFactory(
    private val application: Application,
    private val clothId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClothDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClothDetailViewModel(application, clothId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 