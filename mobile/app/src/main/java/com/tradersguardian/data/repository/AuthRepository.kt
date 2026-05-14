package com.tradersguardian.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.tradersguardian.data.model.*
import com.tradersguardian.data.network.RetrofitClient

class AuthRepository(context: Context) {

    private val api = RetrofitClient.apiService

    private val prefs: SharedPreferences =
        context.getSharedPreferences("tg_prefs", Context.MODE_PRIVATE)

    // ── Session storage ───────────────────────────────────────────────────────

    private fun saveSession(token: String, userId: String) {
        prefs.edit()
            .putString("access_token", token)
            .putString("user_id",      userId)
            .apply()
    }

    fun getToken():   String?  = prefs.getString("access_token", null)
    fun getUserId():  String?  = prefs.getString("user_id",      null)
    fun isLoggedIn(): Boolean  = getToken() != null

    fun clearToken() {
        prefs.edit().remove("access_token").remove("user_id").apply()
    }

    private fun bearer() = "Bearer ${getToken()}"
    private fun eqUserId() = "eq.${getUserId()}"

    // ── Login ─────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): UiState<AuthResponse> {
        return try {
            val response = api.login(request = LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                saveSession(body.accessToken, body.user.id)
                UiState.Success(body)
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    suspend fun register(
        firstName: String,
        lastName:  String,
        email:     String,
        password:  String
    ): UiState<AuthResponse> {
        return try {
            val request = RegisterRequest(
                email    = email,
                password = password,
                data     = mapOf("first_name" to firstName, "last_name" to lastName)
            )
            val response = api.register(request)
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.accessToken.isNotBlank()) {
                    saveSession(body.accessToken, body.user.id)
                }
                UiState.Success(body)
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Account Settings ──────────────────────────────────────────────────────

    suspend fun getSettings(): UiState<AccountSettings> {
        val token  = getToken()  ?: return UiState.Error("Not authenticated")
        val userId = getUserId() ?: return UiState.Error("User ID missing")
        return try {
            val response = api.getSettings(token = "Bearer $token", userId = "eq.$userId")
            if (response.isSuccessful) {
                val list = response.body()
                // If no settings row yet, return safe defaults
                UiState.Success(list?.firstOrNull() ?: AccountSettings(userId = userId))
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun updateSettings(
        accountBalance: Double,
        riskPerTrade:   Double,
        dailyLossLimit: Double
    ): UiState<AccountSettings> {
        val token  = getToken()  ?: return UiState.Error("Not authenticated")
        val userId = getUserId() ?: return UiState.Error("User ID missing")
        return try {
            val request = AccountSettingsRequest(
                userId         = userId,
                accountBalance = accountBalance,
                riskPerTrade   = riskPerTrade,
                dailyLossLimit = dailyLossLimit
            )
            // Upsert: insert or update if user_id already exists
            val response = api.upsertSettings(token = "Bearer $token", request = request)
            if (response.isSuccessful) {
                // Re-fetch to return updated data
                getSettings()
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Trade Plans ───────────────────────────────────────────────────────────

    suspend fun getTrades(): UiState<List<TradePlan>> {
        val token  = getToken()  ?: return UiState.Error("Not authenticated")
        val userId = getUserId() ?: return UiState.Error("User ID missing")
        return try {
            val response = api.getTrades(token = "Bearer $token", userId = "eq.$userId")
            if (response.isSuccessful) {
                UiState.Success(response.body() ?: emptyList())
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun createTrade(request: CreateTradeRequest): UiState<TradePlan> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.createTrade(token = "Bearer $token", request = request)
            if (response.isSuccessful) {
                UiState.Success(response.body()?.firstOrNull() ?: TradePlan())
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun approveTrade(id: Long): UiState<Unit> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.patchTrade(
                token  = "Bearer $token",
                id     = "eq.$id",
                body   = mapOf("status" to "APPROVED")
            )
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun disapproveTrade(id: Long, reason: String): UiState<Unit> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.patchTrade(
                token = "Bearer $token",
                id    = "eq.$id",
                body  = mapOf("status" to "DISAPPROVED", "disapproval_reason" to reason)
            )
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun deleteTrade(id: Long): UiState<Unit> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.deleteTrade(token = "Bearer $token", id = "eq.$id")
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Dashboard (combined) ──────────────────────────────────────────────────

    suspend fun getDashboard(): UiState<DashboardData> {
        val settingsResult = getSettings()
        val tradesResult   = getTrades()

        if (settingsResult is UiState.Error) return UiState.Error(settingsResult.message)

        val settings = (settingsResult as? UiState.Success)?.data ?: AccountSettings()
        val trades   = (tradesResult   as? UiState.Success)?.data ?: emptyList()

        val stats = TradeStats(
            totalTrades       = trades.size,
            approvedTrades    = trades.count { it.status.uppercase() == "APPROVED" },
            disapprovedTrades = trades.count { it.status.uppercase() == "DISAPPROVED" }
        )
        return UiState.Success(DashboardData(settings = settings, stats = stats))
    }

    // ── Error parser ──────────────────────────────────────────────────────────

    private fun parseError(body: String?): String {
        if (body.isNullOrBlank()) return "An unknown error occurred"
        return try {
            val json = org.json.JSONObject(body)
            json.optString("msg")
                .ifBlank { json.optString("error_description") }
                .ifBlank { json.optString("message") }
                .ifBlank { json.optString("error") }
                .ifBlank { body }
        } catch (e: Exception) { body }
    }
}
