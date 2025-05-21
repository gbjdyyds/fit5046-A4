package com.example.uidesign.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wear_history",
    foreignKeys = [
        ForeignKey(entity = Cloth::class, parentColumns = ["id"], childColumns = ["clothId"]),
        // 如果有用户表，可以加用户外键
    ],
    indices = [Index("clothId"), Index("uid")]
)
data class WearHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clothId: Int,
    val uid: String,
    val timestamp: Long
)