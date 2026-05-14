package com.tradersguardian.data.model

import com.google.gson.annotations.SerializedName

// ── Sealed UI state ───────────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Idle    : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ── Auth request bodies ───────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String,
    @SerializedName("data")     val data:     Map<String, String> = emptyMap()
)

// ── Supabase GoTrue auth response ─────────────────────────────────────────────

data class AuthResponse(
    @SerializedName("access_token")  val accessToken:  String,
    @SerializedName("token_type")    val tokenType:    String = "bearer",
    @SerializedName("expires_in")    val expiresIn:    Int    = 3600,
    @SerializedName("refresh_token") val refreshToken: String = "",
    @SerializedName("user")          val user:         SupabaseUser
)

data class SupabaseUser(
    @SerializedName("id")            val id:           String,
    @SerializedName("email")         val email:        String,
    @SerializedName("role")          val role:         String          = "authenticated",
    @SerializedName("user_metadata") val userMetadata: Map<String, String> = emptyMap()
)

// ── Account Settings (Supabase table: account_settings) ──────────────────────
// Column names match Spring Boot JPA default snake_case naming strategy

data class AccountSettings(
    @SerializedName("id")                 val id:               Long   = 0,
    @SerializedName("user_id")            val userId:           String = "",
    @SerializedName("account_balance")    val accountBalance:   Double = 10000.0,
    @SerializedName("risk_per_trade")     val riskPerTrade:     Double = 2.0,
    @SerializedName("daily_loss_limit")   val dailyLossLimit:   Double = 5.0,
    @SerializedName("current_daily_loss") val currentDailyLoss: Double = 0.0
)

data class AccountSettingsRequest(
    @SerializedName("user_id")          val userId:         String,
    @SerializedName("account_balance")  val accountBalance: Double,
    @SerializedName("risk_per_trade")   val riskPerTrade:   Double,
    @SerializedName("daily_loss_limit") val dailyLossLimit: Double
)

// ── Trade Plans (Supabase table: trade_plans) ─────────────────────────────────
// Column names match the @Table/@Column definitions in TradePlan.java

data class TradePlan(
    @SerializedName("id")                val id:                Long   = 0,
    @SerializedName("user_id")           val userId:            String = "",
    @SerializedName("symbol")            val symbol:            String = "",
    @SerializedName("trade_type")        val tradeType:         String = "BUY",
    @SerializedName("entry_price")       val entryPrice:        Double = 0.0,
    @SerializedName("stop_loss")         val stopLoss:          Double = 0.0,
    @SerializedName("take_profit")       val takeProfit:        Double = 0.0,
    @SerializedName("position_size")     val positionSize:      Double = 0.0,
    @SerializedName("risk_amount")       val riskAmount:        Double = 0.0,
    @SerializedName("risk_percent")      val riskPercent:       Double = 0.0,
    @SerializedName("notes")             val notes:             String? = null,
    @SerializedName("status")            val status:            String = "PENDING",
    @SerializedName("disapproval_reason")val disapprovalReason: String? = null,
    @SerializedName("created_at")        val createdAt:         String = ""
)

data class CreateTradeRequest(
    @SerializedName("user_id")       val userId:       String,
    @SerializedName("symbol")        val symbol:       String,
    @SerializedName("trade_type")    val tradeType:    String,
    @SerializedName("entry_price")   val entryPrice:   Double,
    @SerializedName("stop_loss")     val stopLoss:     Double,
    @SerializedName("take_profit")   val takeProfit:   Double,
    @SerializedName("position_size") val positionSize: Double,
    @SerializedName("risk_amount")   val riskAmount:   Double,
    @SerializedName("risk_percent")  val riskPercent:  Double,
    @SerializedName("notes")         val notes:        String = "",
    @SerializedName("status")        val status:       String = "PENDING"
)

// ── Dashboard (combined, computed locally) ────────────────────────────────────

data class DashboardData(
    val settings: AccountSettings = AccountSettings(),
    val stats:    TradeStats      = TradeStats()
)

data class TradeStats(
    val totalTrades:       Int = 0,
    val approvedTrades:    Int = 0,
    val disapprovedTrades: Int = 0
)
