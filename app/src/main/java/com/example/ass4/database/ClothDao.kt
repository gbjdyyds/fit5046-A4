package com.example.ass4.database

import androidx.room.*
import com.example.ass4.database.Cloth
import com.example.ass4.database.ClothType
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
    
    @Query("SELECT * FROM clothes WHERE uid = :uid AND (lastWornDate < (strftime('%s', 'now') * 1000 - 365 * 24 * 60 * 60 * 1000) OR lastWornDate IS NULL)")
    fun getClothesNotWornForOneYear(uid: String): Flow<List<Cloth>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(cloth: Cloth)

    @Update
    suspend fun updateCloth(cloth: Cloth)

    @Delete
    suspend fun deleteCloth(cloth: Cloth)

    @Query("DELETE FROM clothes WHERE uid = :uid")
    suspend fun deleteAllForUser(uid: String)

    @Query("UPDATE clothes SET name = :name WHERE id = :id")
    suspend fun updateClothName(id: Int, name: String)

    @Query("UPDATE clothes SET type = :type WHERE id = :id")
    suspend fun updateClothType(id: Int, type: ClothType)

    @Query("UPDATE clothes SET color = :color WHERE id = :id")
    suspend fun updateClothColor(id: Int, color: String)

    @Query("UPDATE clothes SET fabric = :fabric WHERE id = :id")
    suspend fun updateClothFabric(id: Int, fabric: String)

    @Query("UPDATE clothes SET imagePath = :imagePath WHERE id = :id")
    suspend fun updateClothImage(id: Int, imagePath: String)

    @Query("UPDATE clothes SET wearCount = wearCount + 1 WHERE id = :id")
    suspend fun incrementWearCount(id: Int)

    @Query("UPDATE clothes SET lastWornDate = :timestamp WHERE id = :id")
    suspend fun updateLatestWornDate(id: Int, timestamp: Long = System.currentTimeMillis())

    @Insert
    suspend fun insertClothReturnId(cloth: Cloth): Long


}