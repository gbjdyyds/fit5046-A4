package com.example.ass4.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wear_history",
    foreignKeys = [
        ForeignKey(entity = Cloth::class, parentColumns = ["id"], childColumns = ["clothId"]),
    ],
    indices = [Index("clothId"), Index("uid")]
)
data class WearHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clothId: Int,
    val uid: String,
    val timestamp: Long
)