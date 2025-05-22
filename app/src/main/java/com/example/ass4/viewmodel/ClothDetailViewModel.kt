package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass4.database.Cloth
import com.example.ass4.database.ClothType
import com.example.ass4.repository.ClothRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ClothDetailViewModel(application: Application, private val clothId: Int) : AndroidViewModel(application) {
    private val repository: ClothRepository = ClothRepository(application)
    private val currentUserUid: String? = FirebaseAuth.getInstance().currentUser?.uid

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
            if (cloth?.uid == currentUserUid) {
                _cloth.value = cloth
                checkDonationSuggestion(cloth)
            }
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

    fun updateCloth(
        name: String,
        type: ClothType,
        color: String?,
        fabric: String?,
        imagePath: String?,
        lastWornDate: Long?,
        wearCount: Int?,
        createdAt: Long?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _cloth.value?.let { oldCloth ->
                val updated = oldCloth.copy(
                    name = name,
                    type = type,
                    color = color,
                    fabric = fabric,
                    imagePath = imagePath,
                    lastWornDate = lastWornDate,
                    wearCount = wearCount ?: oldCloth.wearCount,
                    createdAt = createdAt ?: oldCloth.createdAt
                )
                repository.updateCloth(updated)
                _cloth.value = updated
            }
        }
    }

    fun deleteClothAndReturn(onDeleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _cloth.value?.let {
                repository.deleteCloth(it)
                onDeleted()
            }
        }
    }

//    fun updateName(name: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (_cloth.value?.uid == currentUserUid) {
//                repository.updateClothName(clothId, name)
//                _cloth.value = _cloth.value?.copy(name = name)
//            }
//        }
//    }
//
//    fun updateType(type: ClothType) {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (_cloth.value?.uid == currentUserUid) {
//                repository.updateClothType(clothId, type)
//                _cloth.value = _cloth.value?.copy(type = type)
//            }
//        }
//    }
//
//    fun updateColor(color: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (_cloth.value?.uid == currentUserUid) {
//                repository.updateClothColor(clothId, color)
//                _cloth.value = _cloth.value?.copy(color = color)
//            }
//        }
//    }
//
//    fun updateFabric(fabric: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (_cloth.value?.uid == currentUserUid) {
//                repository.updateClothFabric(clothId, fabric)
//                _cloth.value = _cloth.value?.copy(fabric = fabric)
//            }
//        }
//    }
//
//    fun updateImage(imagePath: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (_cloth.value?.uid == currentUserUid) {
//                repository.updateClothImage(clothId, imagePath)
//                _cloth.value = _cloth.value?.copy(imagePath = imagePath)
//            }
//        }
//    }
} 