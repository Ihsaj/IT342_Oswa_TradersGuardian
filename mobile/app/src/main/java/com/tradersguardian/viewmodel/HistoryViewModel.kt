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

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _trades = MutableStateFlow<UiState<List<TradePlan>>>(UiState.Loading)
    val trades: StateFlow<UiState<List<TradePlan>>> = _trades.asStateFlow()

    val filterStatus = MutableStateFlow("ALL")   // ALL | PENDING | APPROVED | DISAPPROVED

    private val _actionLoading = MutableStateFlow<Long?>(null)
    val actionLoading: StateFlow<Long?> = _actionLoading.asStateFlow()

    init { loadTrades() }

    fun loadTrades() {
        viewModelScope.launch {
            _trades.value = UiState.Loading
            _trades.value = repository.getTrades()
        }
    }

    fun approve(id: Long) {
        viewModelScope.launch {
            _actionLoading.value = id
            repository.approveTrade(id)
            _actionLoading.value = null
            loadTrades()
        }
    }

    fun disapprove(id: Long, reason: String) {
        viewModelScope.launch {
            _actionLoading.value = id
            repository.disapproveTrade(id, reason)
            _actionLoading.value = null
            loadTrades()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            _actionLoading.value = id
            repository.deleteTrade(id)
            _actionLoading.value = null
            loadTrades()
        }
    }
}
