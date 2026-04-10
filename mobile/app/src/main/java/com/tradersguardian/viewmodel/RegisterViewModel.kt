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

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<AuthResponse>> = _uiState

    // ── Field states ──────────────────────────────────────────────────────────
    val firstName   = MutableStateFlow("")
    val lastName    = MutableStateFlow("")
    val email       = MutableStateFlow("")
    val username    = MutableStateFlow("")
    val password    = MutableStateFlow("")
    val confirmPw   = MutableStateFlow("")
    val termsAgreed = MutableStateFlow(false)

    val firstNameError  = MutableStateFlow<String?>(null)
    val lastNameError   = MutableStateFlow<String?>(null)
    val emailError      = MutableStateFlow<String?>(null)
    val usernameError   = MutableStateFlow<String?>(null)
    val passwordError   = MutableStateFlow<String?>(null)
    val confirmPwError  = MutableStateFlow<String?>(null)
    val termsError      = MutableStateFlow<String?>(null)

    // ── Password strength (0–4) ───────────────────────────────────────────────
    val passwordStrength = MutableStateFlow(0)

    fun updatePassword(value: String) {
        password.value = value
        var score = 0
        if (value.length >= 8)                  score++
        if (value.any { it.isUpperCase() })     score++
        if (value.any { it.isDigit() })         score++
        if (value.any { !it.isLetterOrDigit() }) score++
        passwordStrength.value = score
    }

    // ── Validation ────────────────────────────────────────────────────────────
    private fun validate(): Boolean {
        var ok = true
        val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+\$")

        firstNameError.value  = if (firstName.value.isBlank()) "Required" else null
        lastNameError.value   = if (lastName.value.isBlank()) "Required" else null
        emailError.value      = if (!emailRegex.matches(email.value.trim())) "Enter a valid email" else null
        usernameError.value   = if (username.value.length < 3) "At least 3 characters" else null
        passwordError.value   = if (password.value.length < 8) "At least 8 characters" else null
        confirmPwError.value  = if (password.value != confirmPw.value) "Passwords do not match" else null
        termsError.value      = if (!termsAgreed.value) "You must accept the terms" else null

        listOf(firstNameError, lastNameError, emailError, usernameError,
            passwordError, confirmPwError, termsError).forEach {
            if (it.value != null) ok = false
        }
        return ok
    }

    // ── Register ──────────────────────────────────────────────────────────────
    fun register() {
        if (!validate()) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.register(
                firstName.value.trim(),
                lastName.value.trim(),
                email.value.trim(),
                username.value.trim(),
                password.value
            )
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
