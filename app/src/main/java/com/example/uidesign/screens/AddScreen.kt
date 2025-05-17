package com.example.uidesign.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen() {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    val lightGray = Color(0xFFE0E0E0)
    val darkGray = Color(0xFF757575)
    val requiredColor = Color(0xFFE53935)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Add New Item",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = greenColor
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* 返回上一页 */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = greenColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = lightGreenBg,
                    titleContentColor = greenColor
                )
            )
        },
        
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 表单区域
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 图片上传区
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 标题带有星号
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Item Photo",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = greenColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = " *",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = requiredColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 上传框
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .border(
                                    BorderStroke(1.dp, lightGray),
                                    RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Upload",
                                    tint = greenColor,
                                    modifier = Modifier.size(32.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Click to upload or drag and drop",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = darkGray
                                    )
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "PNG, JPG up to 10MB",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = darkGray
                                    )
                                )
                            }
                        }
                    }
                    
                    // Style 输入框
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 标题带有星号
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Style",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = greenColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = " *",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = requiredColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 输入框
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = { Text("Add custom style") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = lightGray,
                                focusedBorderColor = greenColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                    
                    // Type 输入框 (带下拉箭头)
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 标题带有星号
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Type",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = greenColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = " *",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = requiredColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 带下拉箭头的输入框
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = { Text("Add custom type") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = lightGray,
                                focusedBorderColor = greenColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = darkGray
                                )
                            }
                        )
                    }
                    
                    // Color 输入框
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 标题带有星号
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Color",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = greenColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = " *",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = requiredColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 输入框
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = { Text("Add custom color") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = lightGray,
                                focusedBorderColor = greenColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                    
                    // Fabric Weight 输入框
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 标题带有星号
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Fabric Weight",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = greenColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = " *",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = requiredColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 输入框
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = { Text("Add fabric weight") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = lightGray,
                                focusedBorderColor = greenColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                    
                    // 按钮区域
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        // 取消按钮
                        OutlinedButton(
                            onClick = { /* 取消操作 */ },
                            modifier = Modifier.padding(end = 12.dp),
                            border = BorderStroke(1.dp, lightGray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = darkGray
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        
                        // 保存按钮
                        Button(
                            onClick = { /* 保存操作 */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = greenColor
                            )
                        ) {
                            Text(
                                text = "Save Item",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
} 