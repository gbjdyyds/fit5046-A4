package com.example.uidesign.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uidesign.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    
    // 创建日历数据
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val dates = (1..31).map { it.toString() }
    val calendarItems = mutableListOf<CalendarItem>()
    
    // 添加日期，所有日期数字都是绿色
    for (i in dates.indices) {
        val outfitImage = when (dates[i]) {
            "2" -> R.drawable.outfit_history_1
            "6" -> R.drawable.outfit_history_2
            "10" -> R.drawable.outfit_history_3
            else -> null
        }
        
        calendarItems.add(
            CalendarItem(
                date = dates[i],
                outfitImageRes = outfitImage,
                isToday = dates[i] == "12"
            )
        )
    }
    
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(64.dp),
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Home
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Home",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Home",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        )
                    }
                    
                    // Wardrobe
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Checkroom,
                            contentDescription = "Wardrobe",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Wardrobe",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        )
                    }
                    
                    // Calendar - Active
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Calendar",
                            tint = greenColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Calendar",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = greenColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    
                    // Profile
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Profile",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // 标题 - 进一步增加顶部间距，避免遮挡状态栏和时间显示
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Outfit Calendar",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = greenColor
                ),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
            
            // 星期标题行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
            
            // 日历网格 - 调整大小和间隔，让格子更高、图片更大
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.6f)  // 增加权重，让日历部分更大
                    .padding(start = 10.dp, end = 10.dp),  // 进一步减少左右内边距
                horizontalArrangement = Arrangement.spacedBy(8.dp),  // 稍微减小水平间距
                verticalArrangement = Arrangement.spacedBy(10.dp)    // 稍微减小垂直间距
            ) {
                items(calendarItems) { item ->
                    CalendarDayItem(item = item, greenColor = greenColor, lightGreenBg = lightGreenBg)
                }
            }
            
            // 即将到来的事件标题 - 紧接日历区域
            Text(
                text = "Upcoming Events",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = greenColor
                ),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
            )
            
            // 事件卡片 - 更大、更突出
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp), // 进一步增加底部间距
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 日期指示器
                    Column(
                        modifier = Modifier
                            .background(lightGreenBg, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MAR",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = greenColor
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "12",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = greenColor
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    // 活动内容
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Office Meeting",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Grey Suit",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        )
                    }
                    
                    // 服装图片 - 使用正确的outfit_suit.jpg，更大尺寸
                    Image(
                        painter = painterResource(id = R.drawable.outfit_suit),
                        contentDescription = "Outfit for event",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    item: CalendarItem,
    greenColor: Color,
    lightGreenBg: Color
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.6f) // 进一步增加高度，使单元格更高
            .clip(RoundedCornerShape(12.dp))
            .background(lightGreenBg)
            .border(
                width = if (item.isToday) 2.dp else 0.dp,
                color = if (item.isToday) greenColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // 日期数字 - 全部使用绿色
        Text(
            text = item.date,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Normal,
                color = greenColor
            ),
            modifier = Modifier.padding(top = 8.dp)  // 略微减少顶部间距
        )
        
        // 穿搭图片 - 尺寸更大，更靠上
        item.outfitImageRes?.let { imageRes ->
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Outfit for day ${item.date}",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 26.dp, bottom = 3.dp, start = 3.dp, end = 3.dp), // 减小内边距，让图片尽可能大
                contentScale = ContentScale.Fit
            )
        }
    }
}

data class CalendarItem(
    val date: String,
    val outfitImageRes: Int? = null,
    val isToday: Boolean = false
) 