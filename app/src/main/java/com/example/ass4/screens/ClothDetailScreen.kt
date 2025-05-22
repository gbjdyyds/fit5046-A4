package com.example.ass4.screens

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import com.example.ass4.database.ClothType
import com.example.ass4.viewmodel.ClothDetailViewModel
import com.example.ass4.viewmodel.ClothDetailViewModelFactory
import java.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import coil.compose.AsyncImage
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothDetailScreen(
    navController: NavController,
    clothId: Int,
    viewModel: ClothDetailViewModel = viewModel(factory = ClothDetailViewModelFactory(LocalContext.current.applicationContext as Application, clothId))
) {
    val cloth by viewModel.cloth.collectAsState()
    val isDonationSuggested by viewModel.isDonationSuggested.collectAsState()
    val context = LocalContext.current

    // State for editable fields
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var fabric by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var typeExpanded by remember { mutableStateOf(false) }
    val typeOptions = listOf("CAP", "TOP", "BOTTOM", "SHOES")


    // Add state for debugging fields
    var lastWornDate by remember { mutableStateOf("") }
    var wearCount by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("") }

    // Donation dialog state
    var showDonationDialog by remember { mutableStateOf(false) }

    // State for error handling
    var nameError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }

    // Image picker launcher, saves image to internal storage and updates preview
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        imagePath = uri?.let { copyImageToInternalStorage(context, it) }
    }

    // On cloth loaded, set initial values
    LaunchedEffect(cloth) {
        cloth?.let {
            name = it.name
            type = it.type.name
            color = it.color.orEmpty()
            fabric = it.fabric.orEmpty()
            imagePath = it.imagePath
            // Set debug fields
            lastWornDate = it.lastWornDate?.toString() ?: ""
            wearCount = it.wearCount?.toString() ?: ""
            createdAt = it.createdAt?.toString() ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cloth Detail") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
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
                        // Image with donation icon if needed
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .border(BorderStroke(1.dp, Color(0xFFE0E0E0)), RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { imageLauncher.launch("image/*") },
                            contentAlignment = Alignment.TopEnd
                        ) {
                            if (imageUri != null) {
                                AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                            } else if (imagePath != null) {
                                AsyncImage(model = imagePath, contentDescription = null, modifier = Modifier.fillMaxSize())
                            }
                            if (isDonationSuggested) {
                                IconButton(
                                    onClick = { showDonationDialog = true },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Donation Suggestion",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }

                        // Editable fields
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

                        // Type dropdown
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Type *", color = Color(0xFF2E7D32), fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        focusedBorderColor = Color(0xFF2E7D32)
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

                        InputField("Color", color, { color = it }, false, "")
                        InputField("Fabric", fabric, { fabric = it }, false, "")

                        // Uneditable fields
                        Divider()
                        // Text("Last Worn: ${cloth?.lastWornDate?.let { Date(it).toLocaleString() } ?: "N/A"}")
                        // Text("Wear Count: ${cloth?.wearCount ?: 0}")
                        // Text("Created At: ${cloth?.createdAt?.let { Date(it).toLocaleString() } ?: "N/A"}")
                        // Editable for debugging:
                        InputField(
                            label = "Last Worn Date (timestamp)",
                            value = lastWornDate,
                            onValueChange = { lastWornDate = it },
                            isError = false,
                            errorText = ""
                        )
                        InputField(
                            label = "Wear Count",
                            value = wearCount,
                            onValueChange = { wearCount = it },
                            isError = false,
                            errorText = ""
                        )
                        InputField(
                            label = "Created At (timestamp)",
                            value = createdAt,
                            onValueChange = { createdAt = it },
                            isError = false,
                            errorText = ""
                        )
                    }
                }
            }
            // Action buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            navController.navigate("wardrobe") {
                                popUpTo("cloth_detail") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("Back", fontSize = 16.sp)
                    }
                    Button(
                        onClick = {
                            nameError = name.isBlank()
                            typeError = type.isBlank()
                            val hasError = nameError || typeError
                            if (hasError) {
                                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val typeEnum = try { ClothType.valueOf(type.uppercase()) } catch (e: Exception) { return@Button }
                            viewModel.updateCloth(
                                name = name,
                                type = typeEnum,
                                color = if (color.isBlank()) null else color,
                                fabric = if (fabric.isBlank()) null else fabric,
                                imagePath = imagePath,
                                // Pass debug fields
                                lastWornDate = lastWornDate.toLongOrNull(),
                                wearCount = wearCount.toIntOrNull(),
                                createdAt = createdAt.toLongOrNull()
                            )
                            Toast.makeText(context, "Cloth updated", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Save", fontSize = 16.sp)
                    }
                }
            }
        }
        // Donation dialog
        if (showDonationDialog) {
            AlertDialog(
                onDismissRequest = { showDonationDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteClothAndReturn {
                            navController.navigate("wardrobe") {
                                popUpTo("cloth_detail") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { showDonationDialog = false }) { Text("Cancel") }
                },
                title = { Text("Donation Suggestion") },
                text = { Text("You haven't worn this item for a year. Would you like to donate it to someone in need?") }
            )
        }
    }
}
