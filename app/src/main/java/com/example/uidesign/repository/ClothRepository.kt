package com.example.uidesign.repository

import android.app.Application
import com.example.uidesign.database.ClothDatabase
import com.example.uidesign.database.Cloth
import com.example.uidesign.database.ClothDao
import com.example.uidesign.database.ClothType
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

    fun getClothesNotWornForOneYear(uid: String): Flow<List<Cloth>> {
        return clothDao.getClothesNotWornForOneYear(uid)
    }
    
    // 新增：更新单个属性的方法
    suspend fun updateClothName(id: Int, name: String) {
        clothDao.updateClothName(id, name)
    }
    
    suspend fun updateClothType(id: Int, type: ClothType) {
        clothDao.updateClothType(id, type)
    }
    
    suspend fun updateClothColor(id: Int, color: String) {
        clothDao.updateClothColor(id, color)
    }
    
    suspend fun updateClothFabric(id: Int, fabric: String) {
        clothDao.updateClothFabric(id, fabric)
    }
    
    suspend fun updateClothImage(id: Int, imagePath: String) {
        clothDao.updateClothImage(id, imagePath)
    }
    
    suspend fun incrementWearCount(id: Int) {
        clothDao.incrementWearCount(id)
    }

    suspend fun updateLatestWornDate(id: Int, timestamp: Long = System.currentTimeMillis()) {
        clothDao.updateLatestWornDate(id, timestamp)
    }
} 

