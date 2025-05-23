package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.ass4.database.ClothDatabase
import com.example.ass4.database.EventEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        ClothDatabase::class.java,
        "calendar-db"
    ).build()

    private val _upcomingEvents = MutableStateFlow<List<Pair<LocalDate, String>>>(emptyList())
    val upcomingEvents: StateFlow<List<Pair<LocalDate, String>>> = _upcomingEvents

    init {
        viewModelScope.launch {
            db.eventEntityDao().getAllEvents().collect { list ->
                _upcomingEvents.value = list.map {
                    LocalDate.parse(it.date) to it.content
                }
            }
        }
    }

    fun addEvent(date: LocalDate, content: String) {
        viewModelScope.launch {
            db.eventEntityDao().insertEvent(EventEntity(date = date.toString(), content = content))
        }
    }
    fun removeEvent(event: Pair<LocalDate, String>) {
        viewModelScope.launch {
            db.eventEntityDao().deleteEventByDateAndContent(event.first.toString(), event.second)
        }
    }
}