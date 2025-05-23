package com.example.ass4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Cloth::class, WearHistory::class, EventEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ClothDatabase : RoomDatabase() {
    abstract fun clothDao(): ClothDao
    abstract fun wearHistoryDao(): WearHistoryDao
    abstract fun eventEntityDao(): EventDao
    companion object {
        @Volatile private var INSTANCE: ClothDatabase? = null

        fun getDatabase(context: Context): ClothDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClothDatabase::class.java,
                    "cloth_database"
                ).fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
