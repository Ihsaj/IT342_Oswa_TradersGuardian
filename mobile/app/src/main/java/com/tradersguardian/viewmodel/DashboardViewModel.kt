package com.tradersguardian.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tradersguardian.data.model.DashboardData
import com.tradersguardian.data.model.UiState
import com.tradersguardian.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _dashboardState = MutableStateFlow<UiState<DashboardData>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardData>> = _dashboardState

    val showLogoutDialog = MutableStateFlow(false)

    init { loadDashboard() }

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            _dashboardState.value = repository.getDashboard()
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        repository.clearToken()
        showLogoutDialog.value = false
        onLoggedOut()
    }
}
