package com.example.uidesign.repository

import android.app.Application
import com.example.uidesign.database.Cloth
import com.example.uidesign.database.ClothDao
import com.example.uidesign.database.ClothDatabase
import kotlinx.coroutines.flow.Flow

class ClothRepository(application: Application) {
    private var clothDao: ClothDao = ClothDatabase.getDatabase(application).clothDao()
    
    // Get all clothes
    val allClothes: Flow<List<Cloth>> = clothDao.getAllClothes()
    
    // Get all clothes of current user
    fun getClothesByUser(uid: String): Flow<List<Cloth>> {
        return clothDao.getClothesByUser(uid)
    }

    // get clothes by type
    fun getClothesByType(type: String): Flow<List<Cloth>> {
        return clothDao.getClothesByType(type)
    }
    
    // get particular cloth by cloth id
    suspend fun getClothById(id: Int): Cloth? {
        return clothDao.getClothById(id)
    }
    
    // insert new clothing
    suspend fun insertCloth(cloth: Cloth) {
        clothDao.insertCloth(cloth)
    }
    
    // update cloth info
    suspend fun updateCloth(cloth: Cloth) {
        clothDao.updateCloth(cloth)
    }
    
    // delete cloth from wardrobe and database
    suspend fun deleteCloth(cloth: Cloth) {
        clothDao.deleteCloth(cloth)
    }
    
    
    
} 

