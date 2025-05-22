package com.example.ass4.screens

import android.app.Application
import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass4.R
import com.example.ass4.database.Cloth
import com.example.ass4.database.ClothType
import com.example.ass4.viewmodel.HomeViewModel
import com.example.ass4.viewmodel.HomeViewModelFactory
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.ass4.navigation.BottomNavBar
import androidx.compose.foundation.BorderStroke
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(application))
    val weatherState = viewModel.weather.collectAsState().value
    val userName by viewModel.userName.collectAsState()
    val isTipsExpanded by viewModel.isTipsExpanded.collectAsState()
    val capList by viewModel.capList.collectAsState()
    val topList by viewModel.topList.collectAsState()
    val bottomList by viewModel.bottomList.collectAsState()
    val shoesList by viewModel.shoesList.collectAsState()
    val selectedCap by viewModel.selectedCap.collectAsState()
    val selectedTop by viewModel.selectedTop.collectAsState()
    val selectedBottom by viewModel.selectedBottom.collectAsState()
    val selectedShoes by viewModel.selectedShoes.collectAsState()

    var showSelector by remember { mutableStateOf<ClothType?>(null) }

    val greenColor = Color(0xFF2E7D32)
    val yellowColor = Color(0xFFFFF59D)
    val orangeColor = Color(0xFFFFA726)

    // 权限 launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                getLocationAndFetchWeather(context, viewModel)
            } else {
                // fallback: melbourne
                viewModel.fetchWeatherByLocation(-37.8136, 144.9631)
            }
        }
    )

    // 首次进入时请求权限
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Prepare weather icon URL from OpenWeather API (if available)
    val iconUrl: String? = weatherState?.weather?.firstOrNull()?.icon?.let { "https://openweathermap.org/img/wn/${it}@2x.png" }
    val tipsText = if (weatherState != null) {
        viewModel.getClothingTips(weatherState.main.temp, weatherState.weather.firstOrNull()?.main ?: "")
    } else {
        "T-shirt, Jeans, Long pants"
    }
    LaunchedEffect(weatherState) {
        android.util.Log.d("WeatherDebug", "UI shows temp: ${weatherState?.main?.temp}")
    }
    Scaffold(
        bottomBar = { BottomNavBar(navController, selected = "home") }
    ) { paddingValues ->
        // Check if wardrobe is empty
        val isWardrobeEmpty = capList.isEmpty() && topList.isEmpty() && bottomList.isEmpty() && shoesList.isEmpty()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Move the top section down for better centering
            Spacer(modifier = Modifier.height(32.dp))
            // Top section: Welcome, weather, and tips in a horizontal layout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Welcome and weather info (left)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Welcome, $userName!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = greenColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (iconUrl != null) {
                                AsyncImage(
                                    model = iconUrl,
                                    contentDescription = weatherState?.weather?.firstOrNull()?.main,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = weatherState?.let { "${it.weather.firstOrNull()?.main ?: "-"} · ${it.main.temp.toInt()}°C" } ?: "--",
                                color = greenColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    // Tips card (right, smaller width)
                    Card(
                        modifier = Modifier
                            .width(120.dp)
                            .height(if (isTipsExpanded) 100.dp else 50.dp)
                            .padding(start = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(2.dp, yellowColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { viewModel.toggleTips() }
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Tips", color = orangeColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Icon(
                                    imageVector = if (isTipsExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Expand/Collapse",
                                    tint = orangeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (isTipsExpanded) {
                                Text(
                                    text = tipsText,
                                    color = Color(0xFF8C8D63),
                                    fontSize = 13.sp,
                                    maxLines = 4,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Compact add clothes banner
            if (isWardrobeEmpty) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = greenColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Your wardrobe is empty. Please add clothes.",
                                color = greenColor,
                                fontSize = 14.sp
                            )
                        }
                        TextButton(
                            // TODO: Add navigation to Wardrobe screen here in the future
                            // onClick = { navController?.navigate("Wardrobe") },
                            onClick = {},
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Add", color = greenColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            // Four-grid selector with compact Select/Clear buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                FourGridSelector(
                    capList, selectedCap, R.drawable.baseball_cap, { showSelector = ClothType.CAP }, { viewModel.clearCloth(ClothType.CAP) },
                    topList, selectedTop, R.drawable.shirt, { showSelector = ClothType.TOP }, { viewModel.clearCloth(ClothType.TOP) },
                    bottomList, selectedBottom, R.drawable.pants, { showSelector = ClothType.BOTTOM }, { viewModel.clearCloth(ClothType.BOTTOM) },
                    shoesList, selectedShoes, R.drawable.shoes, { showSelector = ClothType.SHOES }, { viewModel.clearCloth(ClothType.SHOES) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val hasSelected = selectedCap != null || selectedTop != null || selectedBottom != null || selectedShoes != null
                        if (!hasSelected) {
                            Toast.makeText(context, "Please select at least one item!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.confirmOutfit()
                        Toast.makeText(context, "Outfit saved!", Toast.LENGTH_SHORT).show()
                        // 清空所有选择
                        viewModel.clearCloth(ClothType.CAP)
                        viewModel.clearCloth(ClothType.TOP)
                        viewModel.clearCloth(ClothType.BOTTOM)
                        viewModel.clearCloth(ClothType.SHOES)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Confirm",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Dialog for selecting clothes
            when (showSelector) {
                ClothType.CAP -> {
                    SelectClothDialog("Select Cap", capList, onSelect = {
                        viewModel.selectCloth(ClothType.CAP, it)
                        showSelector = null
                    }, onDismiss = { showSelector = null })
                }
                ClothType.TOP -> {
                    SelectClothDialog("Select Top", topList, onSelect = {
                        viewModel.selectCloth(ClothType.TOP, it)
                        showSelector = null
                    }, onDismiss = { showSelector = null })
                }
                ClothType.BOTTOM -> {
                    SelectClothDialog("Select Bottom", bottomList, onSelect = {
                        viewModel.selectCloth(ClothType.BOTTOM, it)
                        showSelector = null
                    }, onDismiss = { showSelector = null })
                }
                ClothType.SHOES -> {
                    SelectClothDialog("Select Shoes", shoesList, onSelect = {
                        viewModel.selectCloth(ClothType.SHOES, it)
                        showSelector = null
                    }, onDismiss = { showSelector = null })
                }
                null -> {}
            }
        }
    }
}

fun getLocationAndFetchWeather(context: Context, viewModel: HomeViewModel) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
            } else {
                viewModel.fetchWeatherByLocation(-37.8136, 144.9631)
            }
        }
    } catch (e: SecurityException) {
        viewModel.fetchWeatherByLocation(-37.8136, 144.9631)
    }
}

@Composable
fun FourGridSelector(
    capList: List<Cloth>, selectedCap: Cloth?, capIcon: Int, onCapSelect: () -> Unit, onCapClear: () -> Unit,
    topList: List<Cloth>, selectedTop: Cloth?, topIcon: Int, onTopSelect: () -> Unit, onTopClear: () -> Unit,
    bottomList: List<Cloth>, selectedBottom: Cloth?, bottomIcon: Int, onBottomSelect: () -> Unit, onBottomClear: () -> Unit,
    shoesList: List<Cloth>, selectedShoes: Cloth?, shoesIcon: Int, onShoesSelect: () -> Unit, onShoesClear: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        SelectGridItem(capIcon, "Cap", selectedCap, onCapSelect, onCapClear)
        SelectGridItem(topIcon, "Top", selectedTop, onTopSelect, onTopClear)
    }
    Spacer(modifier = Modifier.height(18.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        SelectGridItem(bottomIcon, "Bottom", selectedBottom, onBottomSelect, onBottomClear)
        SelectGridItem(shoesIcon, "Shoes", selectedShoes, onShoesSelect, onShoesClear)
    }
}

@Composable
fun SelectGridItem(
    iconRes: Int,
    label: String,
    selectedItem: Cloth?,
    onSelect: () -> Unit,
    onClear: () -> Unit
) {
    val greenColor = Color(0xFF2E7D32)
    Box(
        modifier = Modifier
            .size(190.dp)
            .border(2.dp, greenColor, RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            if (selectedItem?.imagePath != null && selectedItem.imagePath.isNotBlank()) {
                AsyncImage(
                    model = selectedItem.imagePath,
                    contentDescription = label,
                    modifier = Modifier.size(72.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = greenColor,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    Text("Select", color = Color.White, fontSize = 13.sp, maxLines = 1)
                }
                Button(
                    onClick = onClear,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    border = ButtonDefaults.outlinedButtonBorder,
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    Text("Clear", color = greenColor, fontSize = 13.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun SelectClothDialog(
    title: String,
    items: List<Cloth>,
    onSelect: (Cloth) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(item) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.imagePath != null && item.imagePath.isNotBlank()) {
                            AsyncImage(
                                model = item.imagePath,
                                contentDescription = item.name,
                                modifier = Modifier.size(72.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.shirt),
                                contentDescription = item.name,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(item.name, fontWeight = FontWeight.Medium)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}