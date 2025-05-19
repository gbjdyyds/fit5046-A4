package com.example.uidesign.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.uidesign.database.ClothType
import com.example.uidesign.viewmodel.ClothDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothDetailScreen(
    clothId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ClothDetailViewModel = viewModel()
) {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    val redColor = Color(0xFFE53935)
    
    val cloth by viewModel.cloth.collectAsState()
    val isDonationSuggested by viewModel.isDonationSuggested.collectAsState()
    
    var showTypeDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }
    var showFabricDialog by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // 处理选择的图片
            val imagePath = it.toString()
            viewModel.updateImage(imagePath)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Cloth Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Home",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Home",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                        )
                    }
                    
                    // Wardrobe
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Calendar",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Calendar",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                        )
                    }
                    
                    // Profile
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Profile",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        cloth?.let { currentCloth ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(lightGreenBg)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 衣服图片
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(currentCloth.imagePath),
                        contentDescription = currentCloth.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // 捐赠提示
                    if (isDonationSuggested) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            color = redColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Donate",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                    
                    // 上传图片按钮
                    FloatingActionButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        containerColor = greenColor
                    ) {
                        Icon(Icons.Default.Edit, "Change image")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 衣服信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Name
                        OutlinedTextField(
                            value = currentCloth.name,
                            onValueChange = { viewModel.updateName(it) },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = greenColor,
                                focusedLabelColor = greenColor
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Type and Color
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Type
                            OutlinedTextField(
                                value = currentCloth.type.name,
                                onValueChange = { },
                                label = { Text("Type") },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showTypeDialog = true }) {
                                        Icon(Icons.Default.Edit, "Edit type")
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = greenColor,
                                    focusedLabelColor = greenColor
                                )
                            )
                            
                            // Color
                            OutlinedTextField(
                                value = currentCloth.color,
                                onValueChange = { },
                                label = { Text("Color") },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showColorDialog = true }) {
                                        Icon(Icons.Default.Edit, "Edit color")
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = greenColor,
                                    focusedLabelColor = greenColor
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Fabric and Wear Count
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Fabric
                            OutlinedTextField(
                                value = currentCloth.fabric,
                                onValueChange = { },
                                label = { Text("Fabric") },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showFabricDialog = true }) {
                                        Icon(Icons.Default.Edit, "Edit fabric")
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = greenColor,
                                    focusedLabelColor = greenColor
                                ))
                            }
                            
                            // Wear Count
                            OutlinedTextField(
                                value = currentCloth.wearCount.toString(),
                                onValueChange = { },
                                label = { Text("Wear Count") },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = greenColor,
                                    focusedLabelColor = greenColor
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Last Worn Date
                        OutlinedTextField(
                            value = currentCloth.lastWornDate?.let {
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                            } ?: "Never worn",
                            onValueChange = { },
                            label = { Text("Last Worn") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = greenColor,
                                focusedLabelColor = greenColor
                            )
                        )
                    }
                }
            }
        }
    
    // Type Selection Dialog
    if (showTypeDialog) {
        AlertDialog(
            onDismissRequest = { showTypeDialog = false },
            title = { Text("Select Type") },
            text = {
                Column {
                    ClothType.values().forEach { type ->
                        TextButton(
                            onClick = {
                                viewModel.updateType(type)
                                showTypeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(type.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTypeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Color Input Dialog
    if (showColorDialog) {
        var colorInput by remember { mutableStateOf(cloth?.color ?: "") }
        
        AlertDialog(
            onDismissRequest = { showColorDialog = false },
            title = { Text("Enter Color") },
            text = {
                OutlinedTextField(
                    value = colorInput,
                    onValueChange = { colorInput = it },
                    label = { Text("Color") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateColor(colorInput)
                        showColorDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showColorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Fabric Input Dialog
    if (showFabricDialog) {
        var fabricInput by remember { mutableStateOf(cloth?.fabric ?: "") }
        
        AlertDialog(
            onDismissRequest = { showFabricDialog = false },
            title = { Text("Enter Fabric") },
            text = {
                OutlinedTextField(
                    value = fabricInput,
                    onValueChange = { fabricInput = it },
                    label = { Text("Fabric") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateFabric(fabricInput)
                        showFabricDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFabricDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 