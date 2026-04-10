package com.tradersguardian.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.tradersguardian.data.model.AuthResponse
import com.tradersguardian.data.model.DashboardData
import com.tradersguardian.data.model.LoginRequest
import com.tradersguardian.data.model.RegisterRequest
import com.tradersguardian.data.model.UiState
import com.tradersguardian.data.network.RetrofitClient

class AuthRepository(context: Context) {

    private val api = RetrofitClient.apiService

    private val prefs: SharedPreferences =
        context.getSharedPreferences("tg_prefs", Context.MODE_PRIVATE)

    // ── Session storage ───────────────────────────────────────────────────────

    private fun saveSession(token: String, userId: String) {
        prefs.edit()
            .putString("access_token", token)
            .putString("user_id", userId)
            .apply()
    }

    fun getToken(): String?   = prefs.getString("access_token", null)
    fun getUserId(): String?  = prefs.getString("user_id", null)
    fun isLoggedIn(): Boolean = getToken() != null

    fun clearToken() {
        prefs.edit()
            .remove("access_token")
            .remove("user_id")
            .apply()
    }

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
        lastName: String,
        email: String,
        username: String,
        password: String
    ): UiState<AuthResponse> {
        return try {
            val request = RegisterRequest(
                email    = email,
                password = password,
                data     = mapOf(
                    "first_name" to firstName,
                    "last_name"  to lastName,
                    "username"   to username
                )
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

    // ── Dashboard ─────────────────────────────────────────────────────────────

    suspend fun getDashboard(): UiState<DashboardData> {
        val token  = getToken()  ?: return UiState.Error("Not authenticated")
        val userId = getUserId() ?: return UiState.Error("User ID missing")
        return try {
            val response = api.getDashboard(
                token  = "Bearer $token",
                userId = "eq.$userId"          // ← matches renamed param in ApiService
            )
            if (response.isSuccessful) {
                val list = response.body()
                val data = if (list != null && list.isNotEmpty()) {  // ← fixed isNullOrEmpty + first()
                    list[0]
                } else {
                    DashboardData(userId = userId)
                }
                UiState.Success(data)
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
            json.optString("msg")
                .ifBlank { json.optString("error_description") }
                .ifBlank { json.optString("message") }
                .ifBlank { body }
        } catch (e: Exception) { body }
    }
}
