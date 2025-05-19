package com.example.uidesign.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uidesign.database.Cloth
import com.example.uidesign.database.ClothType
import com.example.uidesign.repository.ClothRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ClothDetailViewModel(application: Application, private val clothId: Int) : AndroidViewModel(application) {
    private val repository: ClothRepository = ClothRepository(application)
    
    private val _cloth = MutableStateFlow<Cloth?>(null)
    val cloth: StateFlow<Cloth?> = _cloth
    
    private val _isDonationSuggested = MutableStateFlow(false)
    val isDonationSuggested: StateFlow<Boolean> = _isDonationSuggested
    
    init {
        loadCloth()
    }
    
    private fun loadCloth() {
        viewModelScope.launch(Dispatchers.IO) {
            val cloth = repository.getClothById(clothId)
            _cloth.value = cloth
            checkDonationSuggestion(cloth)
        }
    }
    
    private fun checkDonationSuggestion(cloth: Cloth?) {
        if (cloth?.lastWornDate == null) {
            _isDonationSuggested.value = false
            return
        }
        
        val oneYearAgo = System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000)
        _isDonationSuggested.value = cloth.lastWornDate < oneYearAgo
    }
    
    fun updateName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateClothName(clothId, name)
            _cloth.value = _cloth.value?.copy(name = name)
        }
    }
    
    fun updateType(type: ClothType) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateClothType(clothId, type)
            _cloth.value = _cloth.value?.copy(type = type)
        }
    }
    
    fun updateColor(color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateClothColor(clothId, color)
            _cloth.value = _cloth.value?.copy(color = color)
        }
    }
    
    fun updateFabric(fabric: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateClothFabric(clothId, fabric)
            _cloth.value = _cloth.value?.copy(fabric = fabric)
        }
    }
    
    fun updateImage(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateClothImage(clothId, imagePath)
            _cloth.value = _cloth.value?.copy(imagePath = imagePath)
        }
    }
} 