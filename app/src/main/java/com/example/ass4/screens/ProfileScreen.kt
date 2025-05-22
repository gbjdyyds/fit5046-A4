// ProfileScreen.kt — 用户个人信息和行为数据动态显示
package com.example.ass4.screens

import android.widget.Toast
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current

    val name by viewModel.userName.collectAsState()
    val email by viewModel.email.collectAsState()
    val createdAt = viewModel.getFormattedCreatedAt()
    val totalClothes by viewModel.totalClothes.collectAsState()
    val noShoppingDays by viewModel.noShoppingDays.collectAsState()
    val chartData by viewModel.monthlyRepeatReusage.collectAsState()
    val isEcoWarrior by viewModel.isEcoWarrior.collectAsState()
    val isCollector by viewModel.isCollector.collectAsState()
    val isMinimalist by viewModel.isMinimalist.collectAsState()

    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF1F8E9)
    val subtleRed = Color(0xFFE57373)
    val lightGray = Color(0xFFEEEEEE)

    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
        viewModel.initializeTimeRange()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, selected = "profile") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(lightGray.copy(alpha = 0.3f))
        ) {
            item { ProfileHeader(name, email, createdAt, navController) }
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatCard(totalClothes.toString(), "Items", lightGreenBg)
//                    StatCard("28", "Outfits", lightGreenBg)
                    StatCard("${noShoppingDays} Days", "No Shopping", lightGreenBg)
                }
            }
            item { SectionTitle("My Achievements") }
            item {
                AchievementRow(Icons.Filled.Nature, "Eco Warrior", "30 Days No Shopping", isEcoWarrior)
                AchievementRow(Icons.Filled.Star, "Style Master", "重复使用同一件衣服超过50次", isCollector)
                AchievementRow(Icons.Filled.CheckCircle, "Minimalist", "10~20 Item Wardrobe", isMinimalist)
            }
            item { SectionTitle("Monthly Usage Trend") }
            item { DateRangeDropdowns(viewModel) }

            item {
                ChartCard(data = chartData)
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                SignOutButton(subtleRed, onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                })
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
fun ChartCard(data: List<MonthlyRepeatReusage>) {
    val green = Color(0xFF2E7D32)
    val lightGreen = Color(0xFFF1F8E9)
    Card(modifier = Modifier.padding(16.dp).fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            val points = data.map { it.repeat_count.toFloat() }
            val labels = data.map { it.month }
            val max = (points.maxOrNull() ?: 1f).coerceAtLeast(1f)
            val path = Path()
            val strokePath = Path()
            points.forEachIndexed { i, yVal ->
                val x = width * i / (points.size - 1)
                val y = height - (yVal / max) * height
                if (i == 0) path.moveTo(x, y).also { strokePath.moveTo(x, y) } else path.lineTo(x, y).also { strokePath.lineTo(x, y) }
            }
            path.lineTo(width, height)
            path.lineTo(0f, height)
            path.close()
            drawPath(path, color = lightGreen)
            drawPath(strokePath, color = green, style = Stroke(width = 3f, cap = StrokeCap.Round))
            points.forEachIndexed { i, yVal ->
                val x = width * i / (points.size - 1)
                val y = height - (yVal / max) * height
                drawCircle(color = green, radius = 5f, center = Offset(x, y))
            }
            labels.forEachIndexed { i, label ->
                val x = width * i / (labels.size - 1)
                drawContext.canvas.nativeCanvas.drawText(label, x, height + 28f, android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                })
            }
        }
    }
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


@Composable
fun DateRangeDropdowns(viewModel: ProfileViewModel) {
    val yearMonthOptions = viewModel.generateYearMonthOptions(viewModel.createdAt.value)
    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd by remember { mutableStateOf(false) }
    var selectedStartOption by remember { mutableStateOf(yearMonthOptions.firstOrNull() ?: "") }
    var selectedEndOption by remember { mutableStateOf(yearMonthOptions.lastOrNull() ?: "") }

    val formatter = DateTimeFormatter.ofPattern("yyyy MMM", Locale.ENGLISH)

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Select Time Range", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = selectedStartOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start") },
                    modifier = Modifier.fillMaxWidth().clickable { expandedStart = true },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                DropdownMenu(expanded = expandedStart, onDismissRequest = { expandedStart = false }) {
                    yearMonthOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedStartOption = option
                                expandedStart = false
                                val ymStart = YearMonth.parse(option, formatter)
                                val startMillis = ymStart.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                val ymEnd = YearMonth.parse(selectedEndOption, formatter)
                                val endMillis = ymEnd.atEndOfMonth().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                viewModel.updateSelectedTimeRange(startMillis, endMillis)
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = selectedEndOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("End") },
                    modifier = Modifier.fillMaxWidth().clickable { expandedEnd = true },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                DropdownMenu(expanded = expandedEnd, onDismissRequest = { expandedEnd = false }) {
                    yearMonthOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedEndOption = option
                                expandedEnd = false
                                val ymStart = YearMonth.parse(selectedStartOption, formatter)
                                val startMillis = ymStart.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                val ymEnd = YearMonth.parse(option, formatter)
                                val endMillis = ymEnd.atEndOfMonth().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                viewModel.updateSelectedTimeRange(startMillis, endMillis)
                            }
                        )
                    }
                }
            }
        }
    }
}
