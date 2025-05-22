package com.example.ass4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// -------------------- RegisterActivity --------------------
class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreenPlaceholder()
        }
    }
}

@Composable
fun RegisterScreenPlaceholder() {
    Text("Register Screen Placeholder")
}

// -------------------- ForgotPasswordActivity --------------------
class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreenPlaceholder()
        }
    }
}

@Composable
fun ForgotPasswordScreenPlaceholder() {
    Text("Forgot Password Screen Placeholder")
}

// -------------------- HomeActivity --------------------
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreenPlaceholder()
        }
    }
}

@Composable
fun HomeScreenPlaceholder() {
    Text("Home Screen Placeholder")
}