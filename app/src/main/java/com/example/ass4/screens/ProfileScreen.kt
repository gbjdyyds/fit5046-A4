// ProfileScreen.kt — 用户个人信息和行为数据动态显示
package com.example.ass4.screens

import android.widget.Toast
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ass4.navigation.BottomNavBar
import com.example.ass4.database.MonthlyRepeatReusage
import com.example.ass4.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.ass4.components.BarChartCard
import android.view.MotionEvent
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val name by viewModel.userName.collectAsState()
    val email by viewModel.email.collectAsState()
    val createdAt = viewModel.getFormattedCreatedAt()
    val totalClothes by viewModel.totalClothes.collectAsState()
    val noShoppingDays by viewModel.noShoppingDays.collectAsState()
    val chartData by viewModel.monthlyRepeatReusage.collectAsState()
    val isEcoWarrior by viewModel.isEcoWarrior.collectAsState()
    val isCollector by viewModel.isCollector.collectAsState()
    val isMinimalist by viewModel.isMinimalist.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadAvailableMonths()
        viewModel.refreshUserInfo()
        viewModel.loadProfileData()
        viewModel.initializeTimeRange()
    }

    Scaffold(bottomBar = { BottomNavBar(navController, selected = "profile") }) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFEEEEEE).copy(alpha = 0.3f))) {

            item { ProfileHeader(name, email, createdAt, navController) }

            item {
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatCard("$totalClothes", "Items", Color.White)
                    StatCard("$noShoppingDays Days", "No Shopping", Color.White)
                }
            }

            item { SectionTitle("My Achievements") }

            item {
                AchievementRow(Icons.Filled.Nature, "Eco Warrior", "30 Days No Shopping", isEcoWarrior)
                AchievementRow(Icons.Filled.Star, "Style Master", "Reuse same cloth >50 times", isCollector)
                AchievementRow(Icons.Filled.CheckCircle, "Minimalist", "10~20 Items", isMinimalist)
            }

            item { SectionTitle("Monthly Usage Trend") }

            item { DateRangeDropdowns(viewModel) }

            item {
                BarChartCard(
                    data = chartData,
                    selectedMonth = selectedMonth,
                    onBarSelected = { selected ->
                        viewModel.setSelectedMonth(selected)
                        selected?.let {
                            val count = chartData.find { it.month == selected }?.repeat_count ?: 0
                            Toast.makeText(context, "$it: $count wears", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                SignOutButton(Color.Red) {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileHeader(name: String, email: String, createdAt: String, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = email, fontSize = 14.sp, color = Color.Gray)
                Text(text = "Member since $createdAt", fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(onClick = { navController.navigate("editProfile") }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, modifier = Modifier.padding(16.dp), style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
}

@Composable
fun SignOutButton(color: Color, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Icon(Icons.Default.ExitToApp, contentDescription = "Sign out", tint = color)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign Out", fontSize = 16.sp)
    }
}

@Composable
fun StatCard(number: String, label: String, backgroundColor: Color) {
    Card(
        modifier = Modifier.width(100.dp).height(70.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = number, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = TextStyle(fontSize = 12.sp, color = Color.DarkGray))
        }
    }
}

@Composable
fun AchievementRow(icon: ImageVector, title: String, subtitle: String, achieved: Boolean) {
    val iconColor = if (achieved) Color(0xFF2E7D32) else Color.Gray
    val bgColor = if (achieved) Color(0xFFF1F8E9) else Color.LightGray
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black))
                Text(text = subtitle, style = TextStyle(fontSize = 14.sp, color = Color.Gray))
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeDropdowns(viewModel: ProfileViewModel) {
    val options = viewModel.getDisplayMonthOptions()
    val formatter = DateTimeFormatter.ofPattern("yyyy MMM", Locale.ENGLISH)

    var selectedStart by remember { mutableStateOf(options.firstOrNull() ?: "") }
    var selectedEnd by remember { mutableStateOf(options.lastOrNull() ?: "") }

    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Select Time Range", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Start Date
            ExposedDropdownMenuBox(
                expanded = expandedStart,
                onExpandedChange = { expandedStart = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedStart,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStart)
                    },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedStart,
                    onDismissRequest = { expandedStart = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedStart = option
                                expandedStart = false
                                updateTimeRange(selectedStart, selectedEnd, viewModel)
                            }
                        )
                    }
                }
            }

            // End Date
            ExposedDropdownMenuBox(
                expanded = expandedEnd,
                onExpandedChange = { expandedEnd = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedEnd,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("End") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEnd)
                    },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedEnd,
                    onDismissRequest = { expandedEnd = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedEnd = option
                                expandedEnd = false
                                updateTimeRange(selectedStart, selectedEnd, viewModel)
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun updateTimeRange(
    startStr: String,
    endStr: String,
    viewModel: ProfileViewModel
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy MMM", Locale.ENGLISH)
    val startYM = YearMonth.parse(startStr, formatter)
    val endYM = YearMonth.parse(endStr, formatter)

    val (finalStart, finalEnd) = if (startYM.isAfter(endYM)) endYM to startYM else startYM to endYM

    val zoneId = ZoneId.systemDefault()
    val startMillis = finalStart.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    val endMillis = finalEnd.atEndOfMonth().atStartOfDay(zoneId).toInstant().toEpochMilli()

    // ✅ 清除选中柱子（防止旧柱残留高亮）
    viewModel.resetSelectedMonth()

    // 🔁 更新 ViewModel 时间范围，并触发图表数据更新
    viewModel.updateSelectedTimeRange(startMillis, endMillis)
}

