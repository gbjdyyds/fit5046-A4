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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.io.File
import java.io.FileOutputStream

// Utility function to copy the selected image to app's internal storage and return its path
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

    // Color constants for UI
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    val lightGray = Color(0xFFE0E0E0)
    val darkGray = Color(0xFF757575)

    // State for each input field, matching Cloth class attributes
    var name by remember { mutableStateOf("") }           // Clothing name
    var type by remember { mutableStateOf("") }           // Clothing type (enum as string)
    var color by remember { mutableStateOf("") }          // Clothing color
    var fabric by remember { mutableStateOf("") }         // Clothing fabric
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Uri for preview
    var imagePath by remember { mutableStateOf<String?>(null) } // Saved image path
    var typeExpanded by remember { mutableStateOf(false) }  // Dropdown menu state
    val typeOptions = listOf("CAP", "TOP", "BOTTOM", "SHOES") // Enum options

    // Info dialog state for cloth type
    var showTypeInfoDialog by remember { mutableStateOf(false) }

    // Error state for each required field
    var nameError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }

    // Image picker launcher, saves image to internal storage and updates preview
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        imagePath = uri?.let { copyImageToInternalStorage(context, it) }
        if (uri != null && imagePath == null) {
            Toast.makeText(context, "Failed to copy image", Toast.LENGTH_SHORT).show()
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
            // Main card for input fields of cloth details
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
                        // Image upload and preview
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
                                    // Preview the selected image
                                    AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                                } else {
                                    //Empty Placeholder UI
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = greenColor)
                                        Text("Click to upload", color = darkGray, fontSize = 14.sp)
                                        Text("PNG, JPG up to 10MB", color = darkGray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Name input field
                        InputField(
                            label = "Name *",
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = it.isBlank()
                            },
                            isError = nameError,
                            errorText = "Name is required"
                        )


                        // Type, a dropdown list of predefined option: cap, top, bottom and shoes
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Type *", color = greenColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.width(4.dp))
                                IconButton(onClick = { showTypeInfoDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Type Info",
                                        tint = greenColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
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
                        // Show info dialog for type
                        if (showTypeInfoDialog) {
                            AlertDialog(
                                onDismissRequest = { showTypeInfoDialog = false },
                                confirmButton = {
                                    TextButton(onClick = { showTypeInfoDialog = false }) {
                                        Text("Got it")
                                    }
                                },
                                title = { Text("Clothing Type Guide") },
                                text = {
                                    Text(
                                        """
                                        • CAP: Hat, cap, beanie
                                        • TOP: T-shirt, hoodie, jacket
                                        • BOTTOM: Pants, jeans, skirt
                                        • SHOES: Sneakers, boots, sandals
                                        """.trimIndent()
                                    )
                                }
                            )
                        }

                        // Color input field (not required)
                        InputField(
                            label = "Color",
                            value = color,
                            onValueChange = { color = it },
                            isError = false,
                            errorText = ""
                        )

                        // Fabric input field (not required)
                        InputField(
                            label = "Fabric",
                            value = fabric,
                            onValueChange = { fabric = it },
                            isError = false,
                            errorText = ""
                        )
                    }
                }
            }
            // Action buttons (Cancel and Save)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // "Cancel" button
                    OutlinedButton(
                        onClick = {
                            // Reset all fields and navigate back
                            name = ""
                            type = ""
                            color = ""
                            fabric = ""
                            imageUri = null
                            imagePath = null
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

                    // "Save" button
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                        onClick = {

                            // Validate all required fields
                            nameError = name.isBlank()
                            typeError = type.isBlank()
                            val hasError = nameError || typeError
                            if (hasError) {
                                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Robust protection that ensure only valid cloth type is saved, and prevent data anomalies
                            val typeEnum = try {
                                ClothType.valueOf(type.uppercase())
                            } catch (e: Exception) {
                                Toast.makeText(context, "Invalid type", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Save the cloth using ViewModel
                            viewModel.saveCloth(
                                name = name,
                                type = typeEnum,
                                color = if (color.isBlank()) null else color,
                                fabric = if (fabric.isBlank()) null else fabric,
                                imageUri = imagePath
                            )
                            Toast.makeText(context, "Item saved to wardrobe", Toast.LENGTH_SHORT).show()

                            // Reset all fields after saving
                            name = ""
                            type = ""
                            color = ""
                            fabric = ""
                            imageUri = null
                            imagePath = null
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

// Reusable input field composable for text input
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorText: String,
    greenColor: Color = Color(0xFF2E7D32),
    lightGray: Color = Color(0xFFE0E0E0)
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("$label", color = greenColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
