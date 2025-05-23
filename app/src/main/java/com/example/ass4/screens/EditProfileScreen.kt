package com.example.ass4.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ass4.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    initialName: String = "",
    initialEmail: String = "",
    onChangePassword: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current
    val greenColor = Color(0xFF2E7D32)

    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = greenColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = greenColor)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email") },
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
//                modifier = Modifier.fillMaxWidth()
//            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = greenColor)
                ) {
                    Text("Cancel")
                }

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    onClick = {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            user.updateEmail(email).addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                                    user.updateProfile(profileUpdates).addOnCompleteListener { nameTask ->
                                        if (nameTask.isSuccessful) {
                                            viewModel.refreshUserInfo()
                                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Failed to update name", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to update email", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }

            TextButton(
                onClick = onChangePassword,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Change Password", color = greenColor)
            }
        }
    }
}
