package com.tradersguardian.data.network

import com.tradersguardian.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Supabase GoTrue Auth ──────────────────────────────────────────────────

    /** Login: POST /auth/v1/token?grant_type=password */
    @POST("auth/v1/token")
    suspend fun login(
        @Query("grant_type") grantType: String = "password",
        @Body request: LoginRequest
    ): Response<AuthResponse>

    /** Register: POST /auth/v1/signup */
    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    // ── Account Settings (table: account_settings) ────────────────────────────

    @GET("rest/v1/account_settings")
    suspend fun getSettings(
        @Header("Authorization") token: String,
        @Query("user_id")        userId: String,   // format: "eq.{uuid}"
        @Query("select")         select: String = "*"
    ): Response<List<AccountSettings>>

    /** Upsert settings — Prefer header handled per-call in repository */
    @POST("rest/v1/account_settings")
    suspend fun upsertSettings(
        @Header("Authorization") token:   String,
        @Header("Prefer")        prefer:  String = "resolution=merge-duplicates",
        @Body request: AccountSettingsRequest
    ): Response<Unit>

    // ── Trade Plans (table: trade_plans) ─────────────────────────────────────

    @GET("rest/v1/trade_plans")
    suspend fun getTrades(
        @Header("Authorization") token:   String,
        @Query("user_id")        userId:  String,   // "eq.{uuid}"
        @Query("select")         select:  String = "*",
        @Query("order")          order:   String = "created_at.desc"
    ): Response<List<TradePlan>>

    @POST("rest/v1/trade_plans")
    suspend fun createTrade(
        @Header("Authorization") token:   String,
        @Header("Prefer")        prefer:  String = "return=representation",
        @Body request: CreateTradeRequest
    ): Response<List<TradePlan>>

    /** PATCH a single trade by id — used for approve / disapprove */
    @PATCH("rest/v1/trade_plans")
    suspend fun patchTrade(
        @Header("Authorization") token:  String,
        @Query("id")             id:     String,   // "eq.{id}"
        @Body body: Map<String, String>
    ): Response<Unit>

    @DELETE("rest/v1/trade_plans")
    suspend fun deleteTrade(
        @Header("Authorization") token:  String,
        @Query("id")             id:     String    // "eq.{id}"
    ): Response<Unit>

    // ── Stats — computed from trade list, no separate endpoint needed ─────────
}
