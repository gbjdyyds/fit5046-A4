package com.example.uidesign.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "clothes")
data class Cloth(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,                // Firebase UID of current user / cloth owner
    val name: String,              // clothing name
    val type: ClothType,           // type：CAP, TOP, BOTTOM, SHOES
    val color: String,
    val fabric: String,
    val lastWornDate: Long?,
    val wearCount: Int,
    val imagePath: String?,         // local path of target image
    val isDonated: Boolean = false
)