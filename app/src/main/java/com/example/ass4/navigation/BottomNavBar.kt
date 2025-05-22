package com.example.ass4.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController, selected: String = "home") {
    val greenColor = Color(0xFF2E7D32)

    BottomAppBar(
        modifier = Modifier.height(64.dp),
        containerColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem("Home", Icons.Outlined.Home, selected == "home", greenColor) {
                navigateSingleTopTo(navController, "home")
            }
            BottomNavItem("Wardrobe", Icons.Outlined.Checkroom, selected == "wardrobe", greenColor) {
                navigateSingleTopTo(navController, "wardrobe")
            }
            BottomNavItem("Calendar", Icons.Outlined.CalendarToday, selected == "calendar", greenColor) {
                navigateSingleTopTo(navController, "calendar")
            }
            BottomNavItem("Profile", Icons.Outlined.Person, selected == "profile", greenColor) {
                navigateSingleTopTo(navController, "profile")
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val color = if (selected) activeColor else Color.Gray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                color = color,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        )
    }
}

fun navigateSingleTopTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
