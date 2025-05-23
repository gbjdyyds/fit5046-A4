package com.example.ass4.database

import androidx.room.*
import com.example.ass4.database.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    // add new booked event into table
    @Insert
    suspend fun insertEvent(event: Event)

    // update event information
    @Update
    suspend fun updateEvent(event: Event)

    // delete booked event
    @Delete
    suspend fun deleteEvent(event: Event)
    
    // delete a specific booked event with event id
    @Query("DELETE FROM booked_Events WHERE eventId = :eventId")
    suspend fun deleteSpecificEvent(eventId: Int)

    // get all booked event for the specific (current) user
    @Query("SELECT * FROM booked_Events WHERE uid = :uid")
    fun getBookedEventsByUser(uid: String): Flow<List<Event>>

    // get all upcoming booked event for the specific (current) user
    @Query("SELECT * FROM booked_Events WHERE uid = :uid AND eventTimestamp >= :currentTime")
    fun getUpcomingEventsByUser(uid: String, currentTime: Long): Flow<List<Event>>
}