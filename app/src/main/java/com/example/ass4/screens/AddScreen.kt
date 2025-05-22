package com.example.ass4.screens

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ass4.database.ClothType
import com.example.ass4.navigation.BottomNavBar
import com.example.ass4.viewmodel.AddClothViewModel
import com.example.ass4.viewmodel.AddClothViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

fun copyImageToInternalStorage(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "cloth_${System.currentTimeMillis()}.jpg"
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val file = File(imagesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: AddClothViewModel = viewModel(factory = AddClothViewModelFactory(application))

    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    val lightGray = Color(0xFFE0E0E0)
    val darkGray = Color(0xFF757575)

    var style by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var fabricWeight by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImagePath by remember { mutableStateOf<String?>(null) }
    var typeExpanded by remember { mutableStateOf(false) }
    val typeOptions = listOf("CAP", "TOP", "BOTTOM", "SHOES")

    var styleError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }
    var colorError by remember { mutableStateOf(false) }
    var fabricWeightError by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            savedImagePath = copyImageToInternalStorage(context, uri)
            if (savedImagePath == null) {
                Toast.makeText(context, "Failed to copy image", Toast.LENGTH_SHORT).show()
            }
        } else {
            savedImagePath = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add New Item", color = greenColor) })
        },
        bottomBar = { BottomNavBar(navController, selected = "add") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(lightGreenBg)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column {
                            Text("Item Photo *", color = greenColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .border(BorderStroke(1.dp, lightGray), RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { imageLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUri != null) {
                                    AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = greenColor)
                                        Text("Click to upload", color = darkGray, fontSize = 14.sp)
                                        Text("PNG, JPG up to 10MB", color = darkGray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        @Composable
                        fun inputField(label: String, value: String, onValueChange: (String) -> Unit, isError: Boolean, errorText: String) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("$label *", color = greenColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = onValueChange,
                                    isError = isError,
                                    placeholder = { Text("Add $label") },
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
                                if (isError) Text(errorText, color = Color.Red, fontSize = 12.sp)
                            }
                        }

                        inputField("Style", style, {
                            style = it
                            styleError = it.isBlank()
                        }, styleError, "Style is required")

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Type *", color = greenColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(8.dp))
                            ExposedDropdownMenuBox(
                                expanded = typeExpanded,
                                onExpandedChange = { typeExpanded = !typeExpanded }
                            ) {
                                OutlinedTextField(
                                    value = type,
                                    onValueChange = {},
                                    readOnly = true,
                                    isError = typeError,
                                    placeholder = { Text("Select type") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.White,
                                        focusedContainerColor = Color.White,
                                        unfocusedBorderColor = lightGray,
                                        focusedBorderColor = greenColor
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                                ExposedDropdownMenu(
                                    expanded = typeExpanded,
                                    onDismissRequest = { typeExpanded = false }
                                ) {
                                    typeOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                type = option
                                                typeExpanded = false
                                                typeError = false
                                            }
                                        )
                                    }
                                }
                            }
                            if (typeError) Text("Type is required", color = Color.Red, fontSize = 12.sp)
                        }

                        inputField("Color", color, {
                            color = it
                            colorError = it.isBlank()
                        }, colorError, "Color is required")

                        inputField("Fabric Weight", fabricWeight, {
                            fabricWeight = it
                            fabricWeightError = it.isBlank()
                        }, fabricWeightError, "Fabric Weight is required")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            style = ""
                            type = ""
                            color = ""
                            fabricWeight = ""
                            imageUri = null
                            navController.navigate("wardrobe") {
                                popUpTo("add") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = greenColor)
                    ) {
                        Text("Cancel", fontSize = 16.sp)
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                        onClick = {
                            val hasError = style.isBlank() || type.isBlank() || color.isBlank() || fabricWeight.isBlank()
                            styleError = style.isBlank()
                            typeError = type.isBlank()
                            colorError = color.isBlank()
                            fabricWeightError = fabricWeight.isBlank()

                            if (hasError) {
                                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid == null) {
                                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val typeEnum = try {
                                ClothType.valueOf(type.uppercase())
                            } catch (e: Exception) {
                                Toast.makeText(context, "Invalid type", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            viewModel.saveCloth(
                                uid = uid,
                                name = style,
                                type = typeEnum,
                                color = color,
                                fabric = fabricWeight,
                                imageUri = savedImagePath
                            )

                            Toast.makeText(context, "Item saved to wardrobe", Toast.LENGTH_SHORT).show()

                            style = ""
                            type = ""
                            color = ""
                            fabricWeight = ""
                            imageUri = null

                            navController.navigate("wardrobe") {
                                popUpTo("add") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text("Save Item", fontSize = 16.sp)
                    }

                }
            }
        }
    }
}