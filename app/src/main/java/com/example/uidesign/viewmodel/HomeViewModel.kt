package com.example.uidesign.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uidesign.database.Cloth
import com.example.uidesign.database.ClothType
import com.example.uidesign.database.WearHistory
import com.example.uidesign.network.WeatherApiService
import com.example.uidesign.network.WeatherResponse
import com.example.uidesign.repository.ClothRepository
import com.example.uidesign.repository.WearHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val clothRepo = ClothRepository(application)
    private val wearHistoryRepo = WearHistoryRepository(application)
    private val weatherApi = WeatherApiService.create()
    private val apiKey = "f68cad898ed653358aefda32b2ae868a" // 你的 OpenWeather API Key

    // 假设你有当前登录用户ID
    var currentUid: String = "Alice" // 实际开发中请从登录获取

    // 天气
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    // 用户名
    private val _userName = MutableStateFlow("Alice")
    val userName: StateFlow<String> = _userName

    // tips 展开
    private val _isTipsExpanded = MutableStateFlow(true)
    val isTipsExpanded: StateFlow<Boolean> = _isTipsExpanded

    // 衣柜数据
    private val _capList = MutableStateFlow<List<Cloth>>(emptyList())
    private val _topList = MutableStateFlow<List<Cloth>>(emptyList())
    private val _bottomList = MutableStateFlow<List<Cloth>>(emptyList())
    private val _shoesList = MutableStateFlow<List<Cloth>>(emptyList())

    val capList: StateFlow<List<Cloth>> = _capList
    val topList: StateFlow<List<Cloth>> = _topList
    val bottomList: StateFlow<List<Cloth>> = _bottomList
    val shoesList: StateFlow<List<Cloth>> = _shoesList

    // 当前选择
    private val _selectedCap = MutableStateFlow<Cloth?>(null)
    private val _selectedTop = MutableStateFlow<Cloth?>(null)
    private val _selectedBottom = MutableStateFlow<Cloth?>(null)
    private val _selectedShoes = MutableStateFlow<Cloth?>(null)

    val selectedCap: StateFlow<Cloth?> = _selectedCap
    val selectedTop: StateFlow<Cloth?> = _selectedTop
    val selectedBottom: StateFlow<Cloth?> = _selectedBottom
    val selectedShoes: StateFlow<Cloth?> = _selectedShoes

    init {
        // Only load wardrobe here. Weather is fetched by Activity after permission/location logic.
        loadWardrobe()
    }

    fun fetchWeatherByCity(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = weatherApi.getCurrentWeatherByCity(city, apiKey)
                android.util.Log.d("WeatherDebug", "API result for city $city: temp=${result.main.temp}")
                _weather.value = result
            } catch (e: Exception) {
                android.util.Log.e("WeatherDebug", "API error for city $city: ${e.message}")
                _weather.value = null
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = weatherApi.getCurrentWeatherByLocation(lat, lon, apiKey)
                android.util.Log.d("WeatherDebug", "API result for lat=$lat, lon=$lon: temp=${result.main.temp}")
                _weather.value = result
            } catch (e: Exception) {
                android.util.Log.e("WeatherDebug", "API error for lat=$lat, lon=$lon: ${e.message}")
                _weather.value = null
            }
        }
    }

    fun toggleTips() {
        _isTipsExpanded.value = !_isTipsExpanded.value
    }

    fun loadWardrobe() {
        viewModelScope.launch {
            clothRepo.getClothesByUser(currentUid).collect { allClothes ->
                _capList.value = allClothes.filter { it.type == ClothType.CAP }
                _topList.value = allClothes.filter { it.type == ClothType.TOP }
                _bottomList.value = allClothes.filter { it.type == ClothType.BOTTOM }
                _shoesList.value = allClothes.filter { it.type == ClothType.SHOES }
            }
        }
    }

    fun selectCloth(type: ClothType, cloth: Cloth) {
        when (type) {
            ClothType.CAP -> _selectedCap.value = cloth
            ClothType.TOP -> _selectedTop.value = cloth
            ClothType.BOTTOM -> _selectedBottom.value = cloth
            ClothType.SHOES -> _selectedShoes.value = cloth
        }
    }

    fun clearCloth(type: ClothType) {
        when (type) {
            ClothType.CAP -> _selectedCap.value = null
            ClothType.TOP -> _selectedTop.value = null
            ClothType.BOTTOM -> _selectedBottom.value = null
            ClothType.SHOES -> _selectedShoes.value = null
        }
    }

    // Confirm 逻辑：保存穿搭历史，更新衣物穿搭次数
    fun confirmOutfit() {
        val selected = listOfNotNull(
            _selectedCap.value,
            _selectedTop.value,
            _selectedBottom.value,
            _selectedShoes.value
        )
        if (selected.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            for (cloth in selected) {
                // 保存穿搭历史
                wearHistoryRepo.insertWearHistory(
                    WearHistory(
                        clothId = cloth.id,
                        uid = currentUid,
                        timestamp = now
                    )
                )
                // 更新衣物穿搭次数和最后穿搭时间
                clothRepo.incrementWearCount(cloth.id)
                clothRepo.updateLatestWornDate(cloth.id, now)
            }
        }
    }
} 