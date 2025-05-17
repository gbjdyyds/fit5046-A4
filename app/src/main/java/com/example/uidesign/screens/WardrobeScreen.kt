package com.example.uidesign.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uidesign.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen() {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    val redColor = Color(0xFFE53935)
    
    // 准备衣橱数据
    val wardrobeItems = listOf(
        WardrobeItem(
            name = "Navy Blazer",
            imageRes = R.drawable.outfit_suit,
            wornTimes = 12,
            lastWorn = "2 weeks ago",
            isDonationSuggested = false
        ),
        WardrobeItem(
            name = "Black Pants",
            imageRes = R.drawable.outfit_history_3,
            wornTimes = 30,
            lastWorn = "1 year ago",
            isDonationSuggested = true
        ),
        WardrobeItem(
            name = "Floral Dress",
            imageRes = R.drawable.outfit_dress,
            wornTimes = 5,
            lastWorn = "3 months ago",
            isDonationSuggested = false
        ),
        WardrobeItem(
            name = "Blue Sweater",
            imageRes = R.drawable.blue_sweater,
            wornTimes = 3,
            lastWorn = "2 years ago",
            isDonationSuggested = true
        )
    )
    
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
                    
                    // Wardrobe - Active
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Checkroom,
                            contentDescription = "Wardrobe",
                            tint = greenColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Wardrobe",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = greenColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    
                    // Calendar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Calendar",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Calendar",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Gray
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
                .background(lightGreenBg)
                .padding(horizontal = 16.dp)
        ) {
            // 标题 - 进一步增加顶部间距，使整体下移
            Spacer(modifier = Modifier.height(45.dp))
            Text(
                text = "My Wardrobe",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = greenColor
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            // 搜索栏和添加按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 搜索框
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Search items...") },
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = greenColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 添加按钮
                Button(
                    onClick = { /* TODO: Handle add item */ },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = greenColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add item",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Item",
                        color = Color.White
                    )
                }
            }
            
            // 衣橱网格 - 调整使其接近底部导航栏
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // 使用weight让内容能够均匀分布
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp) // 减少底部内边距
                ) {
                    items(wardrobeItems) { item ->
                        WardrobeItemCard(item = item, redColor = redColor)
                    }
                }
            }
            // 添加一个很小的底部间距，让内容与底部导航栏更接近
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun WardrobeItemCard(item: WardrobeItem, redColor: Color) {
    val greenColor = Color(0xFF2E7D32)
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            // 衣物图片 - 调整位置让图片更居中
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)  // 图片宽度略小于容器
                        .fillMaxHeight(0.95f) // 图片高度略小于容器
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(top = 8.dp), // 向下移动一点，减少顶部空白
                    contentScale = ContentScale.Fit
                )
                
                // 如果需要捐赠，显示礼物图标
                if (item.isDonationSuggested) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CardGiftcard,
                            contentDescription = "Donation suggested",
                            tint = redColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            // 衣物信息
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = item.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = greenColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "Worn ${item.wornTimes} times",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
                
                Text(
                    text = "Last worn: ${item.lastWorn}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = if (item.isDonationSuggested) redColor else Color.Gray
                    )
                )
            }
        }
    }
}

data class WardrobeItem(
    val name: String,
    val imageRes: Int,
    val wornTimes: Int,
    val lastWorn: String,
    val isDonationSuggested: Boolean
) 