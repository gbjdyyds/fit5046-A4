// ProfileViewModel.kt
package com.example.ass4.viewmodel

import com.example.ass4.database.Cloth
import com.example.ass4.database.ClothType
import com.example.ass4.database.WearHistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.example.ass4.database.MonthlyRepeatReusage
import com.example.ass4.repository.ClothRepository
import com.example.ass4.repository.WearHistoryRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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

    private val _maxRepeatCount = MutableStateFlow(0)
    val maxRepeatCount: StateFlow<Int> = _maxRepeatCount.asStateFlow()

    private val _selectedMonth = MutableStateFlow<String?>(null)
    val selectedMonth: StateFlow<String?> = _selectedMonth


    // achievement system
    val isEcoWarrior: StateFlow<Boolean> = noShoppingDays.map { it >= 30 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isCollector: StateFlow<Boolean> = maxRepeatCount.map { it >= 50 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isMinimalist: StateFlow<Boolean> = totalClothes.map { it <= 20 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _availableMonths = MutableStateFlow<List<String>>(emptyList())
    val availableMonths: StateFlow<List<String>> = _availableMonths.asStateFlow()

    fun loadAvailableMonths() {
        viewModelScope.launch {
            val rawMonths = wearHistoryRepository.getAllAvailableMonths(_uid.value)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
            val now = LocalDate.now()
            val fallback = (0..5).map { now.minusMonths(it.toLong()).format(formatter) }.reversed()
            _availableMonths.value = if (rawMonths.isNotEmpty()) rawMonths.sorted() else fallback
        }
    }

    fun getDisplayMonthOptions(): List<String> {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy MMM", Locale.ENGLISH)
        return _availableMonths.value.map {
            YearMonth.parse(it, inputFormat).format(outputFormat)
        }
    }
//    fun getDisplayMonthOptions(): List<String> {
//        return listOf("2024 Dec","2025 Jan", "2025 Feb", "2025 Mar", "2025 Apr", "2025 May")
//    }



    fun refreshUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        _userName.value = user?.displayName ?: "No Name"
        _email.value = user?.email ?: "No Email"
    }

    // Default initialization time range (within the last 6 months)
    fun initializeTimeRange() {
        val start = getDefaultChartStartMillis()
        val end = System.currentTimeMillis()
        _selectedStart.value = start
        _selectedEnd.value = end
        loadMonthlyRepeatReusage(start, end)
    }

    fun updateSelectedTimeRange(start: Long, end: Long) {
        _selectedStart.value = start
        _selectedEnd.value = end
        loadMonthlyRepeatReusage(start, end)
    }

    fun resetSelectedMonth() {
        _selectedMonth.value = null
    }

    fun setSelectedMonth(month: String?) {
        _selectedMonth.value = month
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

        // ✅ fallback：如果 fromDate 在未来导致为空，就退回 6 个月
        if (options.isEmpty()) {
            val fallback = now.minusMonths(5)
            for (i in 0..5) {
                options.add(fallback.plusMonths(i.toLong()).format(formatter))
            }
        }

        return options
    }


    fun loadProfileData() {
        viewModelScope.launch {
            // 获取衣物数量与注册时间
            clothRepository.getClothesByUser(_uid.value).collect { clothes ->
                _totalClothes.value = clothes.size

                val lastAdded = clothes.maxByOrNull { it.createdAt ?: 0L }?.createdAt ?: System.currentTimeMillis()
                val diff = System.currentTimeMillis() - lastAdded
                _noShoppingDays.value = (diff / (1000 * 60 * 60 * 24)).toInt()

                val earliest = clothes.minByOrNull { it.createdAt ?: System.currentTimeMillis() }?.createdAt
                if (earliest != null) {
                    _createdAt.value = earliest
                } else {
                    _createdAt.value = 1725072000000L // 用于测试 2024 年 7 月
                }
            }

            // 获取最大重复穿搭次数
            val maxRepeat = wearHistoryRepository.getMaxRepeatWearCount(_uid.value) ?: 0
            _maxRepeatCount.value = maxRepeat
        }
    }

    fun loadMonthlyRepeatReusage(startMillis: Long, endMillis: Long) {
        viewModelScope.launch {
            val raw = wearHistoryRepository.getMonthlyRepeatReusageTrend(uid.value, startMillis, endMillis)

            val startYM = Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
            val endYM = Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

            val monthRange = mutableListOf<String>()
            var cursor = startYM
            while (!cursor.isAfter(endYM)) {
                monthRange.add(cursor.format(formatter))
                cursor = cursor.plusMonths(1)
            }

            // 仅保留选择范围内的月份数据，并按顺序补 0
            val filtered = monthRange.map { month ->
                MonthlyRepeatReusage(
                    month = month,
                    repeat_count = raw.find { it.month == month }?.repeat_count ?: 0
                )
            }

            _monthlyRepeatReusage.value = filtered
        }
    }

    fun getFormattedCreatedAt(): String {
        return SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date(createdAt.value))
    }

    fun loadProfileAndChartData() {
        viewModelScope.launch {
            val uid = _uid.value
            clothRepository.getClothesByUser(uid).collect { clothes ->
                _totalClothes.value = clothes.size

                val lastAdded = clothes.maxByOrNull { it.createdAt ?: 0L }?.createdAt ?: System.currentTimeMillis()
                val diff = System.currentTimeMillis() - lastAdded
                _noShoppingDays.value = (diff / (1000 * 60 * 60 * 24)).toInt()

                val earliest = clothes.minByOrNull { it.createdAt ?: System.currentTimeMillis() }?.createdAt
                if (earliest != null) _createdAt.value = earliest

                val maxRepeat = wearHistoryRepository.getMaxRepeatWearCount(uid) ?: 0
                _maxRepeatCount.value = maxRepeat

                val start = getDefaultChartStartMillis()
                val end = System.currentTimeMillis()
                _selectedStart.value = start
                _selectedEnd.value = end

                val result = wearHistoryRepository.getMonthlyRepeatReusageTrend(uid, start, end)
                println("🎯 Chart Result = $result") // 打印查看是否有数据
                _monthlyRepeatReusage.value = result
            }
        }
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

    fun insertMockTestData() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val now = LocalDate.now()
            val zoneId = ZoneId.systemDefault()

            // 插入 Cloth（3 件衣服）
            val cloths = listOf(
                Cloth(uid = uid, name = "Mock Shirt", type = ClothType.TOP, color = "Blue", fabric = "Cotton", lastWornDate = null, imagePath = null),
                Cloth(uid = uid, name = "Mock Pants", type = ClothType.BOTTOM, color = "Black", fabric = "Denim", lastWornDate = null, imagePath = null),
                Cloth(uid = uid, name = "Mock Jacket", type = ClothType.TOP, color = "Gray", fabric = "Wool", lastWornDate = null, imagePath = null),
            )
            val clothIds = cloths.map { clothRepository.insertClothReturnId(it).toInt() }

            val wearHistoryList = mutableListOf<WearHistory>()

            // 为每个月生成重复穿搭数据（过去 6 个月）
            for (i in 0..5) {
                val month = now.minusMonths(i.toLong())
                val baseDay = 5  // 从每月第5天开始
                val clothRepeatPerMonth = 3  // 每件衣服重复穿 3 次

                for (clothId in clothIds) {
                    for (j in 0 until clothRepeatPerMonth) {
                        val date = month.withDayOfMonth(baseDay + j)
                            .atStartOfDay(zoneId)
                            .toInstant()
                            .toEpochMilli()

                        wearHistoryList.add(
                            WearHistory(clothId = clothId, uid = uid, timestamp = date)
                        )
                    }
                }
            }

            // 插入数据
            wearHistoryList.forEach {
                wearHistoryRepository.insertWearHistory(it)
            }

            println("✅ Mock wear history inserted: ${wearHistoryList.size} records")
        }
    }


}
