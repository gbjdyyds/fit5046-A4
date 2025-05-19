package com.example.uidesign.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothDao {
    @Query("SELECT * FROM clothes")
    fun getAllClothes(): Flow<List<Cloth>>

    @Query("SELECT * FROM clothes WHERE uid = :uid")
    fun getClothesByUser(uid: String): Flow<List<Cloth>>

    @Query("SELECT * FROM clothes WHERE type = :type")
    fun getClothesByType(type: String): Flow<List<Cloth>>

    @Query("SELECT * FROM clothes WHERE id = :id")
    suspend fun getClothById(id: Int): Cloth?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(cloth: Cloth)

    @Update
    suspend fun updateCloth(cloth: Cloth)

    @Delete
    suspend fun deleteCloth(cloth: Cloth)

    @Query("DELETE FROM clothes WHERE uid = :uid")
    suspend fun deleteAllForUser(uid: String)
}