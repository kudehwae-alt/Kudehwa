package com.example.data.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(authRepository.getToken() != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun login(token: String) {
        authRepository.saveToken(token)
        _isAuthenticated.value = true
    }

    fun logout() {
        authRepository.clear()
        _isAuthenticated.value = false
    }
}
