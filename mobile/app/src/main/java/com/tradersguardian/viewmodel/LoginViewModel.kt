package com.tradersguardian.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tradersguardian.data.model.AuthResponse
import com.tradersguardian.data.model.UiState
import com.tradersguardian.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<AuthResponse>> = _uiState

    // ── Field states ──────────────────────────────────────────────────────────
    val email    = MutableStateFlow("")
    val password = MutableStateFlow("")

    val emailError    = MutableStateFlow<String?>(null)
    val passwordError = MutableStateFlow<String?>(null)

    // ── Validation ────────────────────────────────────────────────────────────
    private fun validate(): Boolean {
        var ok = true
        val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+\$")

        if (!emailRegex.matches(email.value.trim())) {
            emailError.value = "Please enter a valid email"
            ok = false
        } else emailError.value = null

        if (password.value.isBlank()) {
            passwordError.value = "Password is required"
            ok = false
        } else passwordError.value = null

        return ok
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    fun login() {
        if (!validate()) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.login(
                email.value.trim(),
                password.value
            )
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
