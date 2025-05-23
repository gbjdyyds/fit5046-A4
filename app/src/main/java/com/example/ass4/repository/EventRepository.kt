package com.example.ass4.repository

import android.app.Application
import com.example.ass4.database.Cloth
import com.example.ass4.database.Event
import com.example.ass4.database.EventDao
import com.example.ass4.database.ClothDatabase
import kotlinx.coroutines.flow.Flow

class EventRepository (application: Application) {
    private val eventDao = ClothDatabase.getDatabase(application).eventDao()

    suspend fun insertEvent(event: Event) {
        eventDao.insertEvent(event)
    }


    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }

    suspend fun deleteSpecificEvent(eventId: Int){
        eventDao.deleteSpecificEvent(eventId)
    }

    suspend fun getBookedEventsByUser(uid: String): Flow<List<Event>> {
        return eventDao.getBookedEventsByUser(uid)
    }

    suspend fun getUpcomingEventsByUser(
        uid: String,
        currentTime: Long = System.currentTimeMillis()
    ): Flow<List<Event>> {
        return eventDao.getUpcomingEventsByUser(uid, currentTime)
    }
}