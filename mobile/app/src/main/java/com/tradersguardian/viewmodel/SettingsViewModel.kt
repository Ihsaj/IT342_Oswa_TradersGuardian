package com.tradersguardian.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tradersguardian.data.model.*
import com.tradersguardian.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _loadState = MutableStateFlow<UiState<AccountSettings>>(UiState.Loading)
    val loadState: StateFlow<UiState<AccountSettings>> = _loadState.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<AccountSettings>>(UiState.Idle)
    val saveState: StateFlow<UiState<AccountSettings>> = _saveState.asStateFlow()

    val accountBalance = MutableStateFlow("10000")
    val riskPerTrade   = MutableStateFlow("2")
    val dailyLossLimit = MutableStateFlow("5")

    init { loadSettings() }

    fun loadSettings() {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            val result = repository.getSettings()
            _loadState.value = result
            if (result is UiState.Success) {
                accountBalance.value = result.data.accountBalance.toBigDecimal().stripTrailingZeros().toPlainString()
                riskPerTrade.value   = result.data.riskPerTrade.toBigDecimal().stripTrailingZeros().toPlainString()
                dailyLossLimit.value = result.data.dailyLossLimit.toBigDecimal().stripTrailingZeros().toPlainString()
            }
        }
    }

    fun save() {
        val balance = accountBalance.value.toDoubleOrNull() ?: return
        val risk    = riskPerTrade.value.toDoubleOrNull()   ?: return
        val daily   = dailyLossLimit.value.toDoubleOrNull() ?: return
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            _saveState.value = repository.updateSettings(
                accountBalance = balance,
                riskPerTrade   = risk,
                dailyLossLimit = daily
            )
        }
    }

    fun resetSaveState() { _saveState.value = UiState.Idle }

    val showLogoutDialog = MutableStateFlow(false)

    fun logout(onLoggedOut: () -> Unit) {
        repository.clearToken()
        showLogoutDialog.value = false
        onLoggedOut()
    }
}
