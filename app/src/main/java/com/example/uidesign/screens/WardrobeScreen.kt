package com.example.uidesign.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.uidesign.database.Cloth
import com.example.uidesign.viewmodel.WardrobeViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController
import com.example.uidesign.navigation.BottomNavBar
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    navController: NavController,
    onNavigateToAddCloth: () -> Unit,
    onNavigateToClothDetail: (Int) -> Unit,
    viewModel: WardrobeViewModel = viewModel()
) {
    val greenColor = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5F5F5)
    
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    if (currentUserId == null) {
        Text("User not logged in", modifier = Modifier.padding(16.dp))
        return
    }


    // Get all clothes owned by the current user
    val userClothes by viewModel.getCurrentUserClothes(currentUserId).collectAsState(initial = emptyList())

    // Get all clothes which need donation reminder
    val donationReminderClothes by viewModel.getDonationReminderClothes(currentUserId).collectAsState(initial = emptyList())

    // the state fo search field
    var searchQuery by remember { mutableStateOf("") }
    
    // the clothes of the search result
    val filteredClothes = userClothes.filter {
        searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCloth,
                containerColor = greenColor
            ) {
                Icon(Icons.Default.Add, "Add new clothing")
            }
        },
        bottomBar = { BottomNavBar(navController, selected = "wardrobe") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(lightGreenBg)
                .padding(horizontal = 16.dp)
        ) {
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
            
            // Search 
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search items...") },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            
            // Display cloth card using lazy column
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredClothes) { cloth ->
                    ClothCard(
                        cloth = cloth,
                        isDonationSuggested = cloth in donationReminderClothes,
                        onClick = { onNavigateToClothDetail(cloth.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClothCard(
    cloth: Cloth,
    isDonationSuggested: Boolean,
    onClick: () -> Unit
) {
    val greenColor = Color(0xFF2E7D32)
    val redColor = Color(0xFFE53935)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // cloth image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(cloth.imagePath),
                    contentDescription = cloth.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                
                // donation tag
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
            }
            
            // cloth detail
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = cloth.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // wear count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Repeat,
                        contentDescription = "Wear count",
                        tint = greenColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "worn ${cloth.wearCount} times",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // last worn date
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Last worn",
                        tint = greenColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cloth.lastWornDate?.let {
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                        } ?: "Never worn",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
} 