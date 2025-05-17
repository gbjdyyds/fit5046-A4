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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uidesign.R
import kotlin.math.roundToInt
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF1F8E9)
    val paleRed = Color(0xFFFBE9E7)
    val subtleRed = Color(0xFFE57373)
    val lightGray = Color(0xFFEEEEEE)

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
                    
                    // Profile - Active
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = greenColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Profile",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = greenColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(lightGray.copy(alpha = 0.3f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // 头部信息 - 用户名和基本信息，左对齐带头像
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 头像占位符
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "200 × 200",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column {
                                    // 用户名
                                    Text(
                                        text = "Sarah Chen",
                                        style = TextStyle(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    )
                                    
                                    // 电子邮件
                                    Text(
                                        text = "sarah.chen@gmail.com",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        ),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    // 会员信息
                                    Text(
                                        text = "Member since March 2024",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    )
                                }
                            }
                            
                            // 编辑按钮 - 移到个人信息卡片右下角
                            IconButton(
                                onClick = { /* TODO: Edit profile */ },
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit profile",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                
                // 用户统计数据
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 物品数量
                        StatCard(
                            number = "45",
                            label = "Items",
                            backgroundColor = lightGreenBg
                        )
                        
                        // 服装组合数量
                        StatCard(
                            number = "28",
                            label = "Outfits",
                            backgroundColor = lightGreenBg
                        )
                        
                        // 不购物天数
                        StatCard(
                            number = "21Days",
                            label = "No Shopping",
                            backgroundColor = lightGreenBg
                        )
                    }
                }
                
                // 成就标题
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "My Achievements",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }
                }
                
                // 成就列表
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        // 成就1 - 环保战士
                        AchievementRow(
                            icon = Icons.Filled.Nature,
                            title = "Eco Warrior",
                            subtitle = "30 Days No Shopping"
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 成就2 - 风格大师
                        AchievementRow(
                            icon = Icons.Filled.Star,
                            title = "Style Master",
                            subtitle = "50 Unique Combinations"
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 成就3 - 极简主义者
                        AchievementRow(
                            icon = Icons.Filled.CheckCircle,
                            title = "Minimalist",
                            subtitle = "Maintained capsule wardrobe"
                        )
                    }
                }
                
                // 使用统计标题
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Monthly Usage Trend",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // 使用统计图表
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // 折线图
                            Canvas(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val width = size.width
                                val height = size.height
                                val dataPoints = listOf(20f, 40f, 25f, 60f, 35f, 42f)
                                val maxValue = 60f
                                
                                // 网格线
                                val strokeWidth = 1f
                                val gridColor = Color.LightGray.copy(alpha = 0.5f)
                                
                                // 水平网格线
                                for (i in 0..3) {
                                    val y = height - (height * i / 3)
                                    drawLine(
                                        color = gridColor,
                                        start = Offset(0f, y),
                                        end = Offset(width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                                
                                // 绘制数据线
                                val path = Path()
                                val strokePath = Path()
                                
                                for (i in dataPoints.indices) {
                                    val x = width * i / (dataPoints.size - 1)
                                    val y = height - (height * dataPoints[i] / maxValue)
                                    
                                    if (i == 0) {
                                        path.moveTo(x, y)
                                        strokePath.moveTo(x, y)
                                    } else {
                                        path.lineTo(x, y)
                                        strokePath.lineTo(x, y)
                                    }
                                }
                                
                                // 绘制填充区域
                                path.lineTo(width, height)
                                path.lineTo(0f, height)
                                path.close()
                                
                                drawPath(
                                    path = path,
                                    color = lightGreenBg
                                )
                                
                                drawPath(
                                    path = strokePath,
                                    color = greenColor,
                                    style = Stroke(
                                        width = 3f,
                                        cap = StrokeCap.Round
                                    )
                                )
                                
                                // 绘制数据点
                                for (i in dataPoints.indices) {
                                    val x = width * i / (dataPoints.size - 1)
                                    val y = height - (height * dataPoints[i] / maxValue)
                                    
                                    drawCircle(
                                        color = greenColor,
                                        radius = 4f,
                                        center = Offset(x, y)
                                    )
                                }
                                
                                // 添加x轴标签（月份）
                                val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                                for (i in months.indices) {
                                    val x = width * i / (months.size - 1)
                                    drawContext.canvas.nativeCanvas.drawText(
                                        months[i],
                                        x,
                                        height + 30f,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 30f
                                            textAlign = android.graphics.Paint.Align.CENTER
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // 退出按钮
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = { /* TODO: Sign out */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = subtleRed
                            ),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = subtleRed
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = subtleRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign Out",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            )
        }
    }
}

@Composable
fun AchievementRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF1F8E9)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 成就图标
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(greenColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = greenColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 成就文本
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            }
        }
    }
} 