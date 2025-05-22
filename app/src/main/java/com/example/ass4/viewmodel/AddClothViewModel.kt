package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass4.database.Cloth
import com.example.ass4.database.ClothType
import com.example.ass4.repository.ClothRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AddClothViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ClothRepository(application)

    fun saveCloth(
        uid: String,
        name: String,
        type: ClothType,
        color: String,
        fabric: String,
        imageUri: String?
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cloth = Cloth(
            uid = uid,
            name = name,
            type = type,
            color = color,
            fabric = fabric,
            lastWornDate = null,
            wearCount = 0,
            imagePath = imageUri,
            isDonated = false
        )
        viewModelScope.launch {
            repository.insertCloth(cloth)
        }
    }
}
