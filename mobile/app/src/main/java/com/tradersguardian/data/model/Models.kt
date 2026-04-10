package com.tradersguardian.data.model

import com.google.gson.annotations.SerializedName

// ── Sealed UI state ───────────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Idle    : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ── Request bodies ────────────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val data: Map<String, String> = emptyMap()
)

// ── GoTrue auth response ──────────────────────────────────────────────────────

data class AuthResponse(
    @SerializedName("access_token")  val accessToken: String,
    @SerializedName("token_type")    val tokenType: String,
    @SerializedName("expires_in")    val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("user")          val user: SupabaseUser
)

data class SupabaseUser(
    @SerializedName("id")            val id: String,
    @SerializedName("email")         val email: String,
    @SerializedName("role")          val role: String,
    @SerializedName("user_metadata") val userMetadata: Map<String, String> = emptyMap()
)

// ── Dashboard ─────────────────────────────────────────────────────────────────
// @SerializedName values must match your Supabase "dashboard" table column names exactly.

data class DashboardData(
    @SerializedName("id")                 val id: Int = 0,
    @SerializedName("user_id")            val userId: String = "",

    // Account Summary section
    @SerializedName("balance")            val balance: Double = 0.0,
    @SerializedName("risk_pct")           val riskPct: Float = 0f,
    @SerializedName("loss_limit_pct")     val lossLimitPct: Float = 0f,
    @SerializedName("current_loss_pct")   val currentLossPct: Float = 0f,

    // Quick Statistics section
    @SerializedName("total_trades")       val totalTrades: Int = 0,
    @SerializedName("approved_trades")    val approvedTrades: Int = 0,
    @SerializedName("disapproved_trades") val disapprovedTrades: Int = 0,
)
