package com.example.uidesign.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Cloth::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ClothDatabase : RoomDatabase() {
    abstract fun clothDao(): ClothDao

    companion object {
        @Volatile private var INSTANCE: ClothDatabase? = null

        fun getDatabase(context: Context): ClothDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClothDatabase::class.java,
                    "cloth_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
