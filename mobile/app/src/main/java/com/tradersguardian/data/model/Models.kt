package com.tradersguardian.data.model

import com.google.gson.annotations.SerializedName

// ── Sealed UI state ───────────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Idle    : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ── Backend ApiResponse wrapper ───────────────────────────────────────────────

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String = "",
    @SerializedName("data")    val data:    T?     = null,
    @SerializedName("error")   val error:   String? = null
)

// ── Auth request bodies ───────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email")     val email:     String,
    @SerializedName("password")  val password:  String,
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname")  val lastname:  String
)

// ── Auth response bodies ─────────────────────────────────────────────────────

data class LoginResponse(
    @SerializedName("token")    val token:    String,
    @SerializedName("redirect") val redirect: String = ""
)

data class UserResponse(
    @SerializedName("id")        val id:        Long,
    @SerializedName("email")     val email:     String,
    @SerializedName("firstname") val firstname: String? = null,
    @SerializedName("lastname")  val lastname:  String? = null
)

// ── Account Settings (backend entity: account_settings) ──────────────────────

data class AccountSettings(
    @SerializedName("id")               val id:               Long   = 0,
    @SerializedName("accountBalance")   val accountBalance:   Double = 10000.0,
    @SerializedName("riskPerTrade")     val riskPerTrade:     Double = 2.0,
    @SerializedName("dailyLossLimit")   val dailyLossLimit:   Double = 5.0,
    @SerializedName("currentDailyLoss") val currentDailyLoss: Double = 0.0
)

data class AccountSettingsRequest(
    @SerializedName("accountBalance")  val accountBalance: Double,
    @SerializedName("riskPerTrade")    val riskPerTrade:   Double,
    @SerializedName("dailyLossLimit")  val dailyLossLimit: Double
)

// ── Trade Plans (backend entity: trade_plans) ────────────────────────────────

data class TradePlan(
    @SerializedName("id")                val id:                Long    = 0,
    @SerializedName("symbol")            val symbol:            String  = "",
    @SerializedName("tradeType")         val tradeType:         String  = "BUY",
    @SerializedName("entryPrice")        val entryPrice:        Double  = 0.0,
    @SerializedName("stopLoss")          val stopLoss:          Double  = 0.0,
    @SerializedName("takeProfit")        val takeProfit:        Double  = 0.0,
    @SerializedName("positionSize")      val positionSize:      Double  = 0.0,
    @SerializedName("riskAmount")        val riskAmount:        Double  = 0.0,
    @SerializedName("riskPercent")       val riskPercent:       Double  = 0.0,
    @SerializedName("notes")             val notes:             String? = null,
    @SerializedName("status")            val status:            String  = "PENDING",
    @SerializedName("disapprovalReason") val disapprovalReason: String? = null,
    @SerializedName("outcome")           val outcome:           String? = null,
    @SerializedName("profitLossAmount")  val profitLossAmount:  Double? = null,
    @SerializedName("createdAt")         val createdAt:         String  = ""
)

data class CreateTradeRequest(
    @SerializedName("symbol")        val symbol:       String,
    @SerializedName("tradeType")     val tradeType:    String,
    @SerializedName("entryPrice")    val entryPrice:   Double,
    @SerializedName("stopLoss")      val stopLoss:     Double,
    @SerializedName("takeProfit")    val takeProfit:   Double,
    @SerializedName("positionSize")  val positionSize: Double,
    @SerializedName("riskAmount")    val riskAmount:   Double,
    @SerializedName("riskPercent")   val riskPercent:  Double,
    @SerializedName("notes")         val notes:        String = ""
)

data class RecordOutcomeRequest(
    @SerializedName("outcome")           val outcome:           String,
    @SerializedName("profitLossAmount")  val profitLossAmount:  Double? = null
)

// ── Dashboard Stats (from GET /api/trades/stats) ─────────────────────────────

data class DashboardStats(
    @SerializedName("totalTrades")       val totalTrades:       Long   = 0,
    @SerializedName("approvedTrades")    val approvedTrades:    Long   = 0,
    @SerializedName("disapprovedTrades") val disapprovedTrades: Long   = 0,
    @SerializedName("winTrades")         val winTrades:         Long   = 0,
    @SerializedName("lossTrades")        val lossTrades:        Long   = 0,
    @SerializedName("currentDailyLoss")  val currentDailyLoss:  Double = 0.0,
    @SerializedName("dailyLossLimit")    val dailyLossLimit:    Double = 0.0,
    @SerializedName("accountBalance")    val accountBalance:    Double = 0.0
)

// ── Dashboard (combined, fetched from settings + stats endpoints) ─────────────

data class DashboardData(
    val settings: AccountSettings = AccountSettings(),
    val stats:    DashboardStats  = DashboardStats()
)
