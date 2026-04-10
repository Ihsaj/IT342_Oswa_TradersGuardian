package com.tradersguardian.data.network

import com.tradersguardian.data.model.AuthResponse
import com.tradersguardian.data.model.DashboardData
import com.tradersguardian.data.model.LoginRequest
import com.tradersguardian.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/v1/token")
    suspend fun login(
        @Query("grant_type") grantType: String = "password",
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @GET("rest/v1/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 1
    ): Response<List<DashboardData>>
}
