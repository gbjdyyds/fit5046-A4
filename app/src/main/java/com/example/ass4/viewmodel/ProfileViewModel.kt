// ProfileViewModel.kt
package com.example.ass4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass4.database.MonthlyRepeatReusage
import com.example.ass4.repository.ClothRepository
import com.example.ass4.repository.WearHistoryRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val clothRepository = ClothRepository(application)
    private val wearHistoryRepository = WearHistoryRepository(application)

    private val _uid = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid ?: "")
    val uid = _uid.asStateFlow()

    private val user = FirebaseAuth.getInstance().currentUser
    private val _userName = MutableStateFlow(user?.displayName ?: "No Name")
    val userName: StateFlow<String> = _userName

    private val _email = MutableStateFlow(user?.email ?: "No Email")
    val email: StateFlow<String> = _email


//    private val _userName = MutableStateFlow("Sarah Chen")
//    val userName: StateFlow<String> = _userName
//
//    private val _email = MutableStateFlow("sarah.chen@gmail.com")
//    val email: StateFlow<String> = _email

//    private val _createdAt = MutableStateFlow(System.currentTimeMillis() - 100L * 24 * 60 * 60 * 1000) // 100 days ago
    private val _createdAt = MutableStateFlow(System.currentTimeMillis())
    val createdAt: StateFlow<Long> = _createdAt

    private val _totalClothes = MutableStateFlow(0)
    val totalClothes = _totalClothes.asStateFlow()

    private val _noShoppingDays = MutableStateFlow(0)
    val noShoppingDays = _noShoppingDays.asStateFlow()

    private val _monthlyRepeatReusage = MutableStateFlow<List<MonthlyRepeatReusage>>(emptyList())
    val monthlyRepeatReusage: StateFlow<List<MonthlyRepeatReusage>> = _monthlyRepeatReusage.asStateFlow()

    private val _selectedStart = MutableStateFlow(0L)
    val selectedStart: StateFlow<Long> = _selectedStart

    private val _selectedEnd = MutableStateFlow(0L)
    val selectedEnd: StateFlow<Long> = _selectedEnd

    val isEcoWarrior: StateFlow<Boolean> = noShoppingDays.map { it >= 30 }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isCollector: StateFlow<Boolean> = totalClothes.map { it >= 50 }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isMinimalist: StateFlow<Boolean> = totalClothes.map { it in 10..20 }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun updateSelectedTimeRange(start: Long, end: Long) {
        _selectedStart.value = start
        _selectedEnd.value = end
        loadMonthlyRepeatReusage(start, end)
    }

    fun initializeTimeRange() {
        val start = getDefaultChartStartMillis()
        val end = System.currentTimeMillis()
        _selectedStart.value = start
        _selectedEnd.value = end
        loadMonthlyRepeatReusage(start, end)
    }

    fun incrementMonthRange(months: Int) {
        val zone = ZoneId.systemDefault()
        val newStart = LocalDate.ofInstant(Date(_selectedStart.value).toInstant(), zone).plusMonths(months.toLong())
        val newEnd = LocalDate.ofInstant(Date(_selectedEnd.value).toInstant(), zone).plusMonths(months.toLong())
        updateSelectedTimeRange(
            newStart.atStartOfDay(zone).toInstant().toEpochMilli(),
            newEnd.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        )
    }

    fun getDefaultChartStartMillis(): Long {
        val now = LocalDate.now()
        val start = now.minusMonths(5).withDayOfMonth(1)
        return start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun generateYearMonthOptions(from: Long): List<String> {
        val now = LocalDate.now()
        val fromDate = Date(from).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("yyyy MMM", Locale.ENGLISH)
        val options = mutableListOf<String>()
        var cursor = fromDate
        while (!cursor.isAfter(now)) {
            options.add(cursor.format(formatter))
            cursor = cursor.plusMonths(1)
        }
        return options
    }

    fun loadProfileData() {
        viewModelScope.launch {
            clothRepository.getClothesByUser(_uid.value).collect { clothes ->
                _totalClothes.value = clothes.size

                val lastAdded = clothes.maxByOrNull { it.createdAt ?: 0L }?.createdAt ?: System.currentTimeMillis()
                val diff = System.currentTimeMillis() - lastAdded
                _noShoppingDays.value = (diff / (1000 * 60 * 60 * 24)).toInt()

                val earliest = clothes.minByOrNull { it.createdAt ?: System.currentTimeMillis() }?.createdAt
                if (earliest != null) _createdAt.value = earliest
            }
        }
    }

    fun loadMonthlyRepeatReusage(startMillis: Long = _selectedStart.value, endMillis: Long = _selectedEnd.value) {
        viewModelScope.launch {
            val result = wearHistoryRepository.getMonthlyRepeatReusageTrend(_uid.value, startMillis, endMillis)
            _monthlyRepeatReusage.value = result
        }
    }

    fun getFormattedCreatedAt(): String {
        return SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date(createdAt.value))
    }
}
