package com.tradersguardian.data.network

import com.tradersguardian.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ────────────────────────────────────────────────────────────────────

    /** POST /api/auth/login */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    /** POST /api/auth/register */
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<UserResponse>>

    // ── User ────────────────────────────────────────────────────────────────────

    /** GET /api/user/me — current user profile */
    @GET("api/user/me")
    suspend fun me(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    // ── Account Settings ────────────────────────────────────────────────────────

    /** GET /api/settings */
    @GET("api/settings")
    suspend fun getSettings(
        @Header("Authorization") token: String
    ): Response<ApiResponse<AccountSettings>>

    /** PUT /api/settings */
    @PUT("api/settings")
    suspend fun updateSettings(
        @Header("Authorization") token: String,
        @Body request: AccountSettingsRequest
    ): Response<ApiResponse<AccountSettings>>

    // ── Trade Plans ─────────────────────────────────────────────────────────────

    /** GET /api/trades */
    @GET("api/trades")
    suspend fun getTrades(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<TradePlan>>>

    /** POST /api/trades */
    @POST("api/trades")
    suspend fun createTrade(
        @Header("Authorization") token: String,
        @Body request: CreateTradeRequest
    ): Response<ApiResponse<TradePlan>>

    /** PUT /api/trades/{id}/approve */
    @PUT("api/trades/{id}/approve")
    suspend fun approveTrade(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ApiResponse<TradePlan>>

    /** PUT /api/trades/{id}/disapprove */
    @PUT("api/trades/{id}/disapprove")
    suspend fun disapproveTrade(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body body: Map<String, String>
    ): Response<ApiResponse<TradePlan>>

    /** DELETE /api/trades/{id} */
    @DELETE("api/trades/{id}")
    suspend fun deleteTrade(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ApiResponse<Void>>

    /** PUT /api/trades/{id}/outcome — record WIN or LOSS for an approved trade */
    @PUT("api/trades/{id}/outcome")
    suspend fun recordOutcome(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body request: RecordOutcomeRequest
    ): Response<ApiResponse<TradePlan>>

    // ── Dashboard Stats ─────────────────────────────────────────────────────────

    /** GET /api/trades/stats */
    @GET("api/trades/stats")
    suspend fun getStats(
        @Header("Authorization") token: String
    ): Response<ApiResponse<DashboardStats>>
}
