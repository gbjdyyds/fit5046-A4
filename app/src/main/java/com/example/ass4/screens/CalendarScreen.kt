package com.example.ass4.screens

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ass4.R
import com.example.ass4.database.ClothType
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.ass4.database.Cloth
import com.example.ass4.database.WearHistory
import com.example.ass4.navigation.BottomNavBar
import com.example.ass4.repository.ClothRepository
import com.example.ass4.repository.WearHistoryRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import com.example.ass4.viewmodel.HomeViewModel
import com.example.ass4.viewmodel.HomeViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun CalendarScreen(navController: NavHostController) {

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(application))
    val clothRepo = remember { ClothRepository(application) }
    val historyRepo = remember { WearHistoryRepository(application) }
    val coroutineScope = rememberCoroutineScope()

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

// 用户衣物与选中项
    val capList by viewModel.capList.collectAsState()
    val topList by viewModel.topList.collectAsState()
    val bottomList by viewModel.bottomList.collectAsState()
    val shoesList by viewModel.shoesList.collectAsState()

    val selectedCap by viewModel.selectedCap.collectAsState()
    val selectedTop by viewModel.selectedTop.collectAsState()
    val selectedBottom by viewModel.selectedBottom.collectAsState()
    val selectedShoes by viewModel.selectedShoes.collectAsState()

// 日历逻辑
    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)

    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    val selectedYearMonth = YearMonth.of(selectedYear, selectedMonth + 1)
    var showSelector by remember { mutableStateOf<ClothType?>(null) }
    var allClothes by remember { mutableStateOf<List<Cloth>>(emptyList()) }
    var wearMap by remember { mutableStateOf<Map<LocalDate, List<Cloth>>>(emptyMap()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var upcomingEvents by remember { mutableStateOf<List<Pair<LocalDate, String>>>(emptyList()) }


    LaunchedEffect(true) {
        clothRepo.getClothesByUser(uid).collect { clothes ->
            allClothes = clothes
        }
    }

    LaunchedEffect(selectedYearMonth) {
        historyRepo.getWearHistoryForUser(uid).collect { allHistory ->
            val filtered = allHistory.filter {
                val localDate = Instant.ofEpochMilli(it.timestamp)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                localDate.month == selectedYearMonth.month && localDate.year == selectedYearMonth.year
            }
            wearMap = filtered.groupBy {
                Instant.ofEpochMilli(it.timestamp)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
            }.mapValues { entry ->
                entry.value.mapNotNull { h -> allClothes.find { it.id == h.clothId } }
            }
        }
    }

    Scaffold(bottomBar = { BottomNavBar(navController, "calendar") }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Outfit Calendar",
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = greenColor),
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DropdownSelector("Year", (currentYear - 10..currentYear + 10).map { it.toString() }, selectedYear.toString()) {
                    selectedYear = it.toInt()
                }
                DropdownSelector("Month", listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")[selectedMonth]) {
                    selectedMonth = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").indexOf(it)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            DayOfWeekHeader()
            CalendarGrid(
                yearMonth = selectedYearMonth,
                today = today,
                currentMonth = currentMonth,
                currentYear = currentYear,
                wearMap = wearMap,
                onDayClick = {
                    selectedDate = it
                    showDialog = true
                },
                greenColor = greenColor,
                lightGreenBg = lightGreenBg
            )
        }
    }

    if (showDialog) {
        var eventTitle by remember { mutableStateOf("") }
        val selectedCap by viewModel.selectedCap.collectAsState()
        val selectedTop by viewModel.selectedTop.collectAsState()
        val selectedBottom by viewModel.selectedBottom.collectAsState()
        val selectedShoes by viewModel.selectedShoes.collectAsState()

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    val timestamp = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    coroutineScope.launch {
                        listOfNotNull(
                            selectedCap,
                            selectedTop,
                            selectedBottom,
                            selectedShoes
                        ).forEach { cloth ->
                            historyRepo.insertWearHistory(
                                WearHistory(
                                    uid = uid,
                                    clothId = cloth.id,
                                    timestamp = timestamp,
                                    eventTitle = eventTitle.takeIf { it.isNotBlank() }
                                )
                            )
                        }
                        showDialog = false
                    }
                }) {
                    Text("Confirm")
                }
            },
            title = { Text("Add Event") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Event Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Select Outfit", fontWeight = FontWeight.Medium)
                    OutfitPagerSelector(
                        capList to selectedCap,
                        topList to selectedTop,
                        bottomList to selectedBottom,
                        shoesList to selectedShoes,
                        onCapSelect = { showSelector = ClothType.CAP },
                        onCapClear = { viewModel.clearCloth(ClothType.CAP) },
                        onTopSelect = { showSelector = ClothType.TOP },
                        onTopClear = { viewModel.clearCloth(ClothType.TOP) },
                        onBottomSelect = { showSelector = ClothType.BOTTOM },
                        onBottomClear = { viewModel.clearCloth(ClothType.BOTTOM) },
                        onShoesSelect = { showSelector = ClothType.SHOES },
                        onShoesClear = { viewModel.clearCloth(ClothType.SHOES) }
                    )
                }
            }
        )
    }
}

@Composable
fun DropdownSelector(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("$label: $selected")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DayOfWeekHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 16.sp, color = Color.Gray)
            )
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    today: Int,
    currentMonth: Int,
    currentYear: Int,
    wearMap: Map<LocalDate, List<Cloth>>,
    onDayClick: (LocalDate) -> Unit,
    greenColor: Color,
    lightGreenBg: Color
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val dayOfWeekOffset = Calendar.getInstance().apply {
        set(yearMonth.year, yearMonth.monthValue - 1, 1)
    }.get(Calendar.DAY_OF_WEEK) - 1
    val days = (1..daysInMonth).map { firstDay.withDayOfMonth(it) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(350.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(dayOfWeekOffset) {
            Box(modifier = Modifier.size(48.dp)) {}
        }
        items(days) { date ->
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(lightGreenBg)
                    .border(
                        width = if (date.dayOfMonth == today && yearMonth.monthValue - 1 == currentMonth && yearMonth.year == currentYear) 2.dp else 0.dp,
                        color = if (date.dayOfMonth == today && yearMonth.monthValue - 1 == currentMonth && yearMonth.year == currentYear) greenColor else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onDayClick(date) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = greenColor
                )
                wearMap[date]?.firstOrNull()?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it.imagePath),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


