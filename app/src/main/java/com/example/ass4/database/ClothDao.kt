package com.example.ass4.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothDao {
    @Query("SELECT * FROM clothes")
    fun getAllClothes(): Flow<List<Cloth>>

    @Query("SELECT * FROM clothes WHERE id = :id")
    suspend fun getClothById(id: Int): Cloth?

    @Query("SELECT * FROM clothes WHERE uid = :uid")
    fun getClothesByUser(uid: String): Flow<List<Cloth>>

    @Query("SELECT * FROM clothes WHERE uid = :uid AND type = :type")
    fun getClothesByUserAndType(uid: String, type: String): Flow<List<Cloth>>

    @Query("""
            SELECT * 
            FROM clothes 
            WHERE uid = :uid 
              AND (
                lastWornDate < (strftime('%s', 'now') * 1000 - 365 * 24 * 60 * 60 * 1000)
                OR (
                  lastWornDate IS NULL 
                  AND createdAt < (strftime('%s', 'now') * 1000 - 365 * 24 * 60 * 60 * 1000)
                )
              )
        """)
    fun getClothesNotWornForOneYear(uid: String): Flow<List<Cloth>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(cloth: Cloth)

    @Update
    suspend fun updateCloth(cloth: Cloth)

    @Delete
    suspend fun deleteCloth(cloth: Cloth)

    @Query("UPDATE clothes SET wearCount = wearCount + 1 WHERE id = :id")
    suspend fun incrementWearCount(id: Int)

    @Query("UPDATE clothes SET lastWornDate = :timestamp WHERE id = :id")
    suspend fun updateLatestWornDate(id: Int, timestamp: Long = System.currentTimeMillis())


    @Insert
    suspend fun insertClothReturnId(cloth: Cloth): Long

    @Query("SELECT MAX(createdAt) FROM clothes WHERE uid = :uid")
    suspend fun getMostRecentCreatedAt(uid: String): Long?

    @Query("SELECT * FROM clothes WHERE uid = :uid")
    suspend fun getClothesByUserOnce(uid: String): List<Cloth>

    @Query("SELECT MAX(createdAt) FROM clothes WHERE uid = :uid")
    suspend fun getLastClothAddedTime(uid: String): Long?

}