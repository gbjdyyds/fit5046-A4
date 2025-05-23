package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass4.database.Cloth
import com.example.ass4.repository.ClothRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class WardrobeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ClothRepository
    private val currentUserUid: String? = FirebaseAuth.getInstance().currentUser?.uid
    
    init {
        repository = ClothRepository(application)
    }
    
    // Get current user's clothes
    fun getCurrentUserClothes(): Flow<List<Cloth>> {
        return currentUserUid?.let { uid ->
            repository.getClothesByUser(uid)
        } ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    // Get clothes that haven't been worn for a year and needs donation reminder
    fun getDonationReminderClothes(): Flow<List<Cloth>> {
        return currentUserUid?.let { uid ->
            repository.getClothesNotWornForOneYear(uid)
        } ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
} 