package com.example.ass4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.ass4.ui.theme.UIDesignTheme

import androidx.navigation.compose.rememberNavController
import com.example.ass4.navigation.AppNavGraph
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 构建 Google 登录选项
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            UIDesignTheme {
                val navController = rememberNavController()

                // 在 Compose 作用域中注册 launcher
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val idToken = account.idToken
                        if (idToken != null) {
                            val credential = GoogleAuthProvider.getCredential(idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener { authResult ->
                                    if (authResult.isSuccessful) {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                        }
                    } catch (e: ApiException) {
                        e.printStackTrace()
                    }
                }

                AppNavGraph(
                    navController = navController,
                    onGoogleSignInClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen() {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val greenColor = Color(0xFF2E7D32)
    val textFieldShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Bar with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
        ) {
            IconButton(
                onClick = { /* TODO: Handle back navigation */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = greenColor
                )
            }
            Text(
                text = "Create Account",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = greenColor
                ),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        // Form fields
        Text(
            text = "Full Name",
            color = greenColor,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = {
                Text(
                    "Enter your full name",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = textFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenColor,
                unfocusedBorderColor = Color.LightGray,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Text(
            text = "Email",
            color = greenColor,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    "Enter your email",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = textFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenColor,
                unfocusedBorderColor = Color.LightGray,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Text(
            text = "Password",
            color = greenColor,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    "Create a password",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = textFieldShape,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color.Gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenColor,
                unfocusedBorderColor = Color.LightGray,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Text(
            text = "Confirm Password",
            color = greenColor,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = {
                Text(
                    "Confirm your password",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            shape = textFieldShape,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenColor,
                unfocusedBorderColor = Color.LightGray,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Button(
            onClick = { /* TODO: Handle registration */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = greenColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Create Account",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}