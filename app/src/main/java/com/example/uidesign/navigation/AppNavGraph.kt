package com.example.uidesign.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uidesign.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgotPassword") },
                onLoginSuccess = { navController.navigate("home") },
                onGoogleSignInClick = { /* handle */ }
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("home") }
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onEmailSent = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("wardrobe") {
            WardrobeScreen(
                navController = navController,
                onNavigateToAddCloth = { navController.navigate("add") },
                onNavigateToClothDetail = { clothId ->
                    navController.navigate("clothDetail/$clothId")
                }
            )
        }
        composable("clothDetail/{clothId}") { backStackEntry ->
            val clothId = backStackEntry.arguments?.getString("clothId")?.toIntOrNull() ?: return@composable
            ClothDetailScreen(
                navController = navController,
                clothId = clothId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("calendar") {
            CalendarScreen(navController)
        }
        composable("add") {
            AddScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("editProfile") {
            EditProfileScreen(navController)
        }
    }
}