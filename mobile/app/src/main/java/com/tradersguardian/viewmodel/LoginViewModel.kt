package com.tradersguardian.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tradersguardian.data.model.LoginResponse
import com.tradersguardian.data.model.UiState
import com.tradersguardian.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<LoginResponse>> = _uiState

    val email    = MutableStateFlow("")
    val password = MutableStateFlow("")
    val emailError    = MutableStateFlow<String?>(null)
    val passwordError = MutableStateFlow<String?>(null)

    private fun validate(): Boolean {
        val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        emailError.value    = if (!emailRegex.matches(email.value.trim())) "Please enter a valid email" else null
        passwordError.value = if (password.value.isBlank()) "Password is required" else null
        return emailError.value == null && passwordError.value == null
    }

    fun login() {
        if (!validate()) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.login(email.value.trim(), password.value)
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
