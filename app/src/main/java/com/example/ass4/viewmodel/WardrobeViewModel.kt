package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass4.database.Cloth
import com.example.ass4.repository.ClothRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class WardrobeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ClothRepository
    private val currentUserUid: String? = FirebaseAuth.getInstance().currentUser?.uid
    
    init {
        repository = ClothRepository(application)
    }
    
    // Get current user's clothes
    fun getCurrentUserClothes(uid: String): Flow<List<Cloth>> {
        return repository.getClothesByUser(uid)
    }
    
    fun insertCloth(cloth: Cloth) = viewModelScope.launch(Dispatchers.IO) {
        currentUserUid?.let { uid ->
            repository.insertCloth(cloth.copy(uid = uid))
        }
    }
    
    fun updateCloth(cloth: Cloth) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCloth(cloth)
    }
    
    fun deleteCloth(cloth: Cloth) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteCloth(cloth)
    }
    
    // Get clothes that haven't been worn for a year and needs donation reminder
    fun getDonationReminderClothes(uid: String): Flow<List<Cloth>> {
        return repository.getClothesNotWornForOneYear(uid)
    }
} 