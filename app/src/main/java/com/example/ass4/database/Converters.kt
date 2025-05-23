package com.example.ass4.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromClothType(value: ClothType): String {
        return value.name
    }

    @TypeConverter
    fun toClothType(value: String): ClothType {
        return ClothType.fromString(value)
    }
} 