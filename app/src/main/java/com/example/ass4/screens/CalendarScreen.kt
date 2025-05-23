package com.example.ass4.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ass4.navigation.BottomNavBar
import com.example.ass4.viewmodel.CalendarViewModel
import com.example.ass4.viewmodel.CalendarViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

@Composable
fun CalendarScreen(navController: NavHostController) {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    val application = LocalContext.current.applicationContext as Application
    val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModelFactory(application))
    val upcomingEvents by viewModel.upcomingEvents.collectAsState()
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    val selectedYearMonth = YearMonth.of(selectedYear, selectedMonth + 1)

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var eventTitle by remember { mutableStateOf("") }
    var outfitHint by remember { mutableStateOf("") }
    var viewEventDetails by remember { mutableStateOf(false) }
    var selectedEventDetails by remember { mutableStateOf<List<String>>(emptyList()) }
    var eventToDelete by remember { mutableStateOf<Pair<LocalDate, String>?>(null) }
    var showPastDateWarning by remember { mutableStateOf(false) }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                events = upcomingEvents,
                onDayClick = { date ->
                    val todayDate = LocalDate.now()
                    selectedDate = date
                    if (date.isBefore(todayDate)) {
                        showPastDateWarning = true
                    } else {
                        val eventForDate = upcomingEvents.filter { it.first == date }
                        if (eventForDate.isNotEmpty()) {
                            viewEventDetails = true
                            selectedEventDetails = eventForDate.map { it.second }
                        } else {
                            showDialog = true
                        }
                    }
                },
                greenColor = greenColor,
                lightGreenBg = Color(0xFFEEF5EE)
            )

            Text(
                text = "Upcoming Events",
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = greenColor),
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(upcomingEvents.sortedBy { it.first }) { (date, title) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { eventToDelete = date to title }
                    ){
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("$date", color = greenColor, fontWeight = FontWeight.Bold)
                            Text(title, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(

            containerColor = Color(0xFFE8F5E9),
            titleContentColor = Color(0xFF2E7D32),
            textContentColor = Color.DarkGray,
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.addEvent(selectedDate, "$eventTitle - $outfitHint")
                    eventTitle = ""
                    outfitHint = ""
                    showDialog = false
                },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("Confirm", color = Color.White)
                }
            },
            title = {
                Text(
                    text = "Add Event",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            },
            text = {
                Column {
                    TextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Event Title") },

                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = outfitHint,
                        onValueChange = { outfitHint = it },
                        label = { Text("Outfit Hint") },

                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
    if (viewEventDetails) {
        AlertDialog(
            onDismissRequest = { viewEventDetails = false },
            confirmButton = {
                Button(onClick = { viewEventDetails = false },colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("Close",color = Color.White)
                }
            },
            title = { Text("Events on ${selectedDate}") },
            text = {
                Column {
                    selectedEventDetails.forEach {
                        Text("• $it", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        )
    }
    if (eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { eventToDelete = null },   title = {
                Text(
                    text = "Delete Event",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                )
            },
            text = {
                Text("Are you sure you want to delete this event?\n\n${eventToDelete!!.first}: ${eventToDelete!!.second}")
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.removeEvent(eventToDelete!!)
                    eventToDelete = null
                },colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("Delete",color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    eventToDelete = null
                },colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("Cancel",color = Color.White)
                }
            }
        )
    }
    if (showPastDateWarning) {
        AlertDialog(
            onDismissRequest = { showPastDateWarning = false },
            confirmButton = {
                Button(onClick = { showPastDateWarning = false },colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("OK",color = Color.White)
                }
            },title = {
                Text(
                    text = "Invalid Date",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                )
            },
            text = { Text("The selected date has already passed. You can only add events for today or future dates.") }
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
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
            Text(
                text = it,
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
    events: List<Pair<LocalDate, String>>,
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
    val eventDates = events.map { it.first }.toSet()

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
                        width = if (date.dayOfMonth == today && date.monthValue - 1 == currentMonth && date.year == currentYear) 2.dp else 0.dp,
                        color = if (date.dayOfMonth == today && date.monthValue - 1 == currentMonth && date.year == currentYear) greenColor else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onDayClick(date) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = greenColor
                )
                if (eventDates.contains(date)) {
                    Text(text = "*", color = Color.Red, fontSize = 10.sp)
                }
            }
        }
    }
}