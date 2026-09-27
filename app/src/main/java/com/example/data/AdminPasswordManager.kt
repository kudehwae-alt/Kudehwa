package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdminPasswordManager {
    private val _adminPassword = MutableStateFlow("admin123")
    val adminPassword: StateFlow<String> = _adminPassword.asStateFlow()

    fun updatePassword(newPass: String): Boolean {
        if (newPass.length >= 4) {
            _adminPassword.value = newPass
            return true
        }
        return false
    }
}
