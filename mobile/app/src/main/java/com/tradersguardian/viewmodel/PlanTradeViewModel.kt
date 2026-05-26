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
import kotlin.math.abs

class PlanTradeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    // ── Form fields ───────────────────────────────────────────────────────────
    val symbol     = MutableStateFlow("")
    val tradeType  = MutableStateFlow("BUY")
    val entryPrice = MutableStateFlow("")
    val stopLoss   = MutableStateFlow("")
    val takeProfit = MutableStateFlow("")
    val notes      = MutableStateFlow("")

    private val _settings = MutableStateFlow(AccountSettings())
    val settings: StateFlow<AccountSettings> = _settings.asStateFlow()

    private val _calculated = MutableStateFlow<CalculatedRisk?>(null)
    val calculated: StateFlow<CalculatedRisk?> = _calculated.asStateFlow()

    private val _submitState = MutableStateFlow<UiState<TradePlan>>(UiState.Idle)
    val submitState: StateFlow<UiState<TradePlan>> = _submitState.asStateFlow()

    val error = MutableStateFlow<String?>(null)

    init { loadSettings() }

    private fun loadSettings() {
        viewModelScope.launch {
            val result = repository.getSettings()
            if (result is UiState.Success) _settings.value = result.data
        }
    }

    fun calculate() {
        val entry = entryPrice.value.toDoubleOrNull()
        val sl    = stopLoss.value.toDoubleOrNull()
        val tp    = takeProfit.value.toDoubleOrNull()

        if (symbol.value.isBlank() || entry == null || sl == null || tp == null) {
            error.value = "Please fill in Symbol, Entry Price, Stop Loss, and Take Profit."
            return
        }
        error.value = null

        val balance    = _settings.value.accountBalance
        val riskPct    = _settings.value.riskPerTrade
        val riskAmount = balance * riskPct / 100.0
        val priceDiff  = abs(entry - sl)
        val posSize    = if (priceDiff > 0) riskAmount / priceDiff else 0.0
        val reward     = abs(tp - entry) * posSize
        val rr         = if (priceDiff > 0) abs(tp - entry) / priceDiff else 0.0

        _calculated.value = CalculatedRisk(
            riskAmount   = riskAmount,
            riskPercent  = riskPct,
            positionSize = posSize,
            rewardAmount = reward,
            rr           = rr
        )
    }

    fun submit() {
        val calc = _calculated.value ?: run { error.value = "Calculate risk first."; return }
        if (!repository.isLoggedIn()) { error.value = "Not authenticated."; return }

        viewModelScope.launch {
            _submitState.value = UiState.Loading
            _submitState.value = repository.createTrade(
                CreateTradeRequest(
                    symbol       = symbol.value.uppercase().trim(),
                    tradeType    = tradeType.value,
                    entryPrice   = entryPrice.value.toDoubleOrNull() ?: 0.0,
                    stopLoss     = stopLoss.value.toDoubleOrNull() ?: 0.0,
                    takeProfit   = takeProfit.value.toDoubleOrNull() ?: 0.0,
                    positionSize = calc.positionSize,
                    riskAmount   = calc.riskAmount,
                    riskPercent  = calc.riskPercent,
                    notes        = notes.value
                )
            )
        }
    }

    fun resetForm() {
        symbol.value = ""; tradeType.value = "BUY"
        entryPrice.value = ""; stopLoss.value = ""
        takeProfit.value = ""; notes.value = ""
        _calculated.value = null
        _submitState.value = UiState.Idle
        error.value = null
    }
}

data class CalculatedRisk(
    val riskAmount:   Double,
    val riskPercent:  Double,
    val positionSize: Double,
    val rewardAmount: Double,
    val rr:           Double
)
