package com.example.ass4.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class Cloth(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,                // Firebase UID of current user / cloth owner
    val name: String,              // clothing name
    val type: ClothType,           // type：CAP, TOP, BOTTOM, SHOES
    val color: String?,
    val fabric: String?,
    val lastWornDate: Long?,
    val wearCount: Int = 0,
    val imagePath: String?,         // local path of target image
    val isDonated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()    // used to be compute "no shopping day"
)