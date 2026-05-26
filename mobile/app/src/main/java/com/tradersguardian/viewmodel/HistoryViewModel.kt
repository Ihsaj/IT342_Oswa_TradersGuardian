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

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    init { loadTrades() }

    fun clearActionError() { _actionError.value = null }

    fun loadTrades() {
        viewModelScope.launch {
            _trades.value = UiState.Loading
            _trades.value = repository.getTrades()
        }
    }

    fun approve(id: Long) {
        viewModelScope.launch {
            _actionLoading.value = id
            _actionError.value = null
            val result = repository.approveTrade(id)
            if (result is UiState.Error) _actionError.value = result.message
            _actionLoading.value = null
            loadTrades()
        }
    }

    fun disapprove(id: Long, reason: String) {
        viewModelScope.launch {
            _actionLoading.value = id
            _actionError.value = null
            val result = repository.disapproveTrade(id, reason)
            if (result is UiState.Error) _actionError.value = result.message
            _actionLoading.value = null
            loadTrades()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            _actionLoading.value = id
            _actionError.value = null
            val result = repository.deleteTrade(id)
            if (result is UiState.Error) _actionError.value = result.message
            _actionLoading.value = null
            loadTrades()
        }
    }

    fun recordOutcome(id: Long, outcome: String, profitLossAmount: Double?) {
        viewModelScope.launch {
            _actionLoading.value = id
            _actionError.value = null
            val result = repository.recordOutcome(id, outcome, profitLossAmount)
            if (result is UiState.Error) _actionError.value = result.message
            _actionLoading.value = null
            loadTrades()
        }
    }
}
