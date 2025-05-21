package com.example.uidesign.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _registerState = MutableStateFlow<RegisterResult?>(null)
    val registerState = _registerState.asStateFlow()

    fun registerWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registerState.value = RegisterResult.Success
                } else {
                    _registerState.value =
                        RegisterResult.Failure(task.exception?.message ?: "Registration failed")
                }
            }
    }

    sealed class RegisterResult {
        object Success : RegisterResult()
        data class Failure(val message: String) : RegisterResult()
    }
}