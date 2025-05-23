package com.example.ass4.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booked_Events")
data class Event(
    @PrimaryKey(autoGenerate = true) val eventId: Int = 0,
    val uid: String,
    val eventTimestamp: Long,
    val eventTitle: String,
    val eventDescription: String
)