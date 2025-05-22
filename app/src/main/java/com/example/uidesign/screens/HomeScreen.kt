package com.example.uidesign.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.uidesign.R
import com.example.uidesign.navigation.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF1F8E9)

    Scaffold(
        bottomBar = { BottomNavBar(navController, selected = "home") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 顶部空白，下移内容
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Weather Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = lightGreenBg
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Text(
                                    text = "Today's Weather",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = greenColor
                                    )
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Sunny • 20°C",
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            color = greenColor
                                        )
                                    )
                                }
                                
                                Text(
                                    text = "Perfect weather for light layers!",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = greenColor
                                    )
                                )
                            }
                            
                            // Custom loading indicator with shorter and thicker lines
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                Canvas(modifier = Modifier.size(42.dp)) {
                                    val center = Offset(size.width / 2, size.height / 2)
                                    val outerRadius = size.width / 2
                                    // 增加内圆半径，使线条更短
                                    val innerRadius = size.width / 3
                                    
                                    // Draw 8 radiating lines
                                    for (i in 0 until 8) {
                                        val angle = Math.PI / 4 * i
                                        val startX = center.x + innerRadius * kotlin.math.cos(angle).toFloat()
                                        val startY = center.y + innerRadius * kotlin.math.sin(angle).toFloat()
                                        val endX = center.x + outerRadius * kotlin.math.cos(angle).toFloat()
                                        val endY = center.y + outerRadius * kotlin.math.sin(angle).toFloat()
                                        
                                        // 移除透明度差异，所有线条同样粗细和透明度
                                        drawLine(
                                            color = greenColor.copy(alpha = 0.8f),
                                            start = Offset(startX, startY),
                                            end = Offset(endX, endY),
                                            // 进一步增加线条宽度
                                            strokeWidth = 6f,
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Recommended Outfits Title
                item {
                    Text(
                        text = "Recommended Outfits",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Outfit Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(620.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        // 整体布局容器
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 模特与衣服居中显示
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f)
                                    .align(Alignment.TopCenter)
                                    .padding(top = 4.dp, bottom = 30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // 缩小服装图片尺寸
                                Image(
                                    painter = painterResource(id = R.drawable.suit_pic),
                                    contentDescription = "Outfit",
                                    modifier = Modifier
                                        .fillMaxHeight(0.78f)  // 减小高度比例
                                        .fillMaxWidth(0.8f),   // 减小宽度比例
                                    contentScale = ContentScale.Fit
                                )
                            }
                            
                            // 左侧箭头 - 上移到人物中间位置
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.6f) // 减小高度，使箭头上移
                                    .width(48.dp)
                                    .align(Alignment.CenterStart)
                                    .offset(y = (-40).dp), // 向上偏移
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous outfit",
                                    tint = greenColor,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            
                            // 右侧箭头 - 上移到人物中间位置
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.6f) // 减小高度，使箭头上移 
                                    .width(48.dp)
                                    .align(Alignment.CenterEnd)
                                    .offset(y = (-40).dp), // 向上偏移
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowRight,
                                    contentDescription = "Next outfit",
                                    tint = greenColor,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            
                            // 底部衣橱图标 - 调整位置不挡住脚部
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 80.dp) // 减少底部间距，让图标下移一点
                            ) {
                                FloatingActionButton(
                                    onClick = { /* TODO: Open wardrobe */ },
                                    containerColor = greenColor,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Checkroom,
                                        contentDescription = "Wardrobe",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Add some space at the bottom
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
} 