package com.example.ass4.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ass4.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch
import java.util.*

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length > 7 && password.any { it.isLetter() }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel = viewModel(),
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val greenColor = Color(0xFF2E7D32)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val registerState by registerViewModel.registerState.collectAsState()
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            dob = "$year-${month + 1}-$day"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(registerState) {
        when (val result = registerState) {
            is RegisterViewModel.RegisterResult.Success -> onRegisterSuccess()
            is RegisterViewModel.RegisterResult.Failure -> snackbarHostState.showSnackbar(result.message)
            null -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = greenColor)
                }
                Text(
                    text = "Create Account",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium, color = greenColor)
                )
            }

            FormLabel("Full Name", greenColor)
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Enter your full name", color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = fieldColors(greenColor)
            )



            FormLabel("Date of Birth", greenColor)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = dob,
                    onValueChange = {},
                    placeholder = {
                        Text("Select your birth date", color = Color.Gray, fontSize = 16.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray)
                    },
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = fieldColors(greenColor)
                )

                // 透明点击区域盖在上面
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePicker.show() }
                )
            }

            FormLabel("Email", greenColor)
            val isEmailValid = email.isEmpty() || isValidEmail(email)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text("Enter your email", color = Color.Gray, fontSize = 16.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                isError = !isEmailValid,
                supportingText = {
                    if (!isEmailValid) {
                        Text("Please enter a valid email address", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = fieldColors(greenColor)
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                Text("Password", color = greenColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.Info, contentDescription = null, tint = greenColor, modifier = Modifier.size(16.dp))
            }
            val isPasswordValid = isValidPassword(password)

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Create a password", color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                textStyle = TextStyle(fontSize = 16.sp),
                isError = password.isNotEmpty() && !isPasswordValid,
                supportingText = {
                    if (password.isNotEmpty() && !isPasswordValid) {
                        Text("Password must be more than 7 characters and contain at least one letter", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = fieldColors(greenColor)
            )

            FormLabel("Confirm Password", greenColor)
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirm your password", color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = fieldColors(greenColor)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        when {
                            password != confirmPassword -> {
                                snackbarHostState.showSnackbar("Passwords do not match")
                            }
                            password.length < 7 -> {
                                snackbarHostState.showSnackbar("Password must be at least 7 characters long")
                            }
                            !password.any { it.isLetter() } -> {
                                snackbarHostState.showSnackbar("Password must contain at least one letter")
                            }
                            else -> {
                                registerViewModel.registerWithEmail(fullName, email, password)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun FormLabel(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun fieldColors(greenColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = greenColor,
    unfocusedBorderColor = Color.LightGray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)