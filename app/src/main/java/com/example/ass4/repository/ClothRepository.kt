package com.example.ass4.repository

import android.app.Application
import com.example.ass4.database.ClothDatabase
import com.example.ass4.database.Cloth
import com.example.ass4.database.ClothDao
import kotlinx.coroutines.flow.Flow

class ClothRepository(application: Application) {
    private val clothDao: ClothDao = ClothDatabase.getDatabase(application).clothDao()
    
    // Get all clothes
    val allClothes: Flow<List<Cloth>> = clothDao.getAllClothes()
    
    // Get all clothes of current user
    fun getClothesByUser(uid: String): Flow<List<Cloth>> {
        return clothDao.getClothesByUser(uid)
    }

    // get clothes by type
    fun getClothesByType(uid: String, type: String): Flow<List<Cloth>> {
        return clothDao.getClothesByUserAndType(uid, type)
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

    fun getClothesNotWornForOneYear(uid: String): Flow<List<Cloth>> {
        return clothDao.getClothesNotWornForOneYear(uid)
    }
    
    suspend fun incrementWearCount(id: Int) {
        clothDao.incrementWearCount(id)
    }

    suspend fun updateLatestWornDate(id: Int, timestamp: Long = System.currentTimeMillis()) {
        clothDao.updateLatestWornDate(id, timestamp)
    }

    suspend fun insertClothReturnId(cloth: Cloth): Long {
        return clothDao.insertClothReturnId(cloth)
    }

    suspend fun getMostRecentCreatedAt(uid: String): Long? {
        return clothDao.getMostRecentCreatedAt(uid)
    }
} 

