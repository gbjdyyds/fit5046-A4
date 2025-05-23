package com.example.ass4.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ass4.screens.AddScreen
import com.example.ass4.screens.CalendarScreen
import com.example.ass4.screens.ClothDetailScreen
import com.example.ass4.screens.EditProfileScreen
import com.example.ass4.screens.ForgotPasswordScreen
import com.example.ass4.screens.HomeScreen
import com.example.ass4.screens.*
import com.example.ass4.viewmodel.ProfileViewModel

@Composable
fun AppNavGraph(navController: NavHostController,
                onGoogleSignInClick: () -> Unit = {}) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgotPassword") },
                onLoginSuccess = { navController.navigate("home") },
                onGoogleSignInClick = onGoogleSignInClick
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("login") }
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
                clothId = clothId
            )
        }
        composable("calendar") {
            CalendarScreen(navController = navController)
        }
        composable("add") {
            AddScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("editProfile") {
            val viewModel: ProfileViewModel = viewModel()
            EditProfileScreen(
                navController = navController,
                initialName = viewModel.userName.collectAsState().value,
                initialEmail = viewModel.email.collectAsState().value,
                onChangePassword = { navController.navigate("forgotPassword") }
            )
        }

    }
}