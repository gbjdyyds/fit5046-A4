package com.example.uidesign.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ForgotPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _resetState = MutableStateFlow<ResetResult?>(null)
    val resetState = _resetState.asStateFlow()

    fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _resetState.value = if (task.isSuccessful) {
                    ResetResult.Success
                } else {
                    ResetResult.Failure(task.exception?.message ?: "Reset failed")
                }
            }
    }

    sealed class ResetResult {
        object Success : ResetResult()
        data class Failure(val message: String) : ResetResult()
    }
}


