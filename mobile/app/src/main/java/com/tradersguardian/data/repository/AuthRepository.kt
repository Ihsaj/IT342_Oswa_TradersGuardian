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

    private fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun getToken():   String?  = prefs.getString("access_token", null)
    fun isLoggedIn(): Boolean  = getToken() != null

    fun clearToken() {
        prefs.edit().remove("access_token").apply()
    }

    private fun bearer() = "Bearer ${getToken()}"

    // ── Login ─────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): UiState<LoginResponse> {
        return try {
            val response = api.login(request = LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    saveToken(body.data.token)
                    UiState.Success(body.data)
                } else {
                    UiState.Error(body?.message ?: "Login failed")
                }
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
    ): UiState<String> {
        return try {
            val request = RegisterRequest(
                email     = email,
                password  = password,
                firstname = firstName,
                lastname  = lastName
            )
            val response = api.register(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    UiState.Success(body.message)
                } else {
                    UiState.Error(body?.message ?: "Registration failed")
                }
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Account Settings ──────────────────────────────────────────────────────

    suspend fun getSettings(): UiState<AccountSettings> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.getSettings(token = "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                UiState.Success(body?.data ?: AccountSettings())
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
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val request = AccountSettingsRequest(
                accountBalance = accountBalance,
                riskPerTrade   = riskPerTrade,
                dailyLossLimit = dailyLossLimit
            )
            val response = api.updateSettings(token = "Bearer $token", request = request)
            if (response.isSuccessful) {
                val body = response.body()
                UiState.Success(body?.data ?: AccountSettings())
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Trade Plans ───────────────────────────────────────────────────────────

    suspend fun getTrades(): UiState<List<TradePlan>> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.getTrades(token = "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                UiState.Success(body?.data ?: emptyList())
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
                val body = response.body()
                UiState.Success(body?.data ?: TradePlan())
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
            val response = api.approveTrade(token = "Bearer $token", id = id)
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun disapproveTrade(id: Long, reason: String): UiState<Unit> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.disapproveTrade(
                token = "Bearer $token",
                id    = id,
                body  = mapOf("reason" to reason)
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
            val response = api.deleteTrade(token = "Bearer $token", id = id)
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun recordOutcome(id: Long, outcome: String, profitLossAmount: Double?): UiState<Unit> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val request = RecordOutcomeRequest(
                outcome = outcome,
                profitLossAmount = profitLossAmount
            )
            val response = api.recordOutcome(token = "Bearer $token", id = id, request = request)
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Dashboard (combined) ──────────────────────────────────────────────────

    suspend fun getDashboard(): UiState<DashboardData> {
        val settingsResult = getSettings()
        val statsResult    = getStats()

        if (settingsResult is UiState.Error) return UiState.Error(settingsResult.message)

        val settings = (settingsResult as? UiState.Success)?.data ?: AccountSettings()
        val stats    = (statsResult    as? UiState.Success)?.data ?: DashboardStats()

        return UiState.Success(DashboardData(settings = settings, stats = stats))
    }

    private suspend fun getStats(): UiState<DashboardStats> {
        val token = getToken() ?: return UiState.Error("Not authenticated")
        return try {
            val response = api.getStats(token = "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                UiState.Success(body?.data ?: DashboardStats())
            } else {
                UiState.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            UiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    // ── Error parser ──────────────────────────────────────────────────────────

    private fun parseError(body: String?): String {
        if (body.isNullOrBlank()) return "An unknown error occurred"
        return try {
            val json = org.json.JSONObject(body)
            json.optString("message")
                .ifBlank { json.optString("error") }
                .ifBlank { body }
        } catch (e: Exception) { body }
    }
}
