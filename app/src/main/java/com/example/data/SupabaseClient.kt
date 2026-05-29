package com.example.data

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * Technical Specification for Supabase Auth and Database REST integration.
 * Matches standard PostgreSQL tables with PostgREST conventions.
 */
object SupabaseConfig {
    // These keys can be configured in AI Studio Secrets panel.
    // Fallback placeholders allow local development and demonstration.
    var url: String = "https://your-project.supabase.co"
    var apiKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Replace with anon key
    
    val isConfigured: Boolean
        get() = url.isNotEmpty() && !url.contains("your-project") && apiKey.isNotEmpty() && apiKey.length > 50
}

// =========================================================
// Retrofit API Declarations
// =========================================================

interface SupabaseAuthApi {
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body body: Map<String, String>
    ): retrofit2.Response<Map<String, Any>>

    @POST("auth/v1/token")
    suspend fun logIn(
        @Query("grant_type") grantType: String = "password",
        @Body body: Map<String, String>
    ): retrofit2.Response<Map<String, Any>>

    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body body: Map<String, String>
    ): retrofit2.Response<Unit>
}

interface SupabaseDbApi {
    // Onboarding profile sync
    @GET("rest/v1/onboarding_profiles")
    suspend fun getProfile(
        @Query("email") emailFilter: String,
        @Header("Range") range: String = "0-0"
    ): List<Map<String, Any>>

    @POST("rest/v1/onboarding_profiles")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertProfile(
        @Body profile: Map<String, Any>
    ): retrofit2.Response<Unit>

    // Streak sync
    @GET("rest/v1/streaks")
    suspend fun getStreak(
        @Query("email") emailFilter: String
    ): List<Map<String, Any>>

    @POST("rest/v1/streaks")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertStreak(
        @Body streak: Map<String, Any>
    ): retrofit2.Response<Unit>

    // Workouts completed
    @GET("rest/v1/completed_workouts")
    suspend fun getCompletedWorkouts(
        @Query("userEmail") emailFilter: String
    ): List<Map<String, Any>>

    @POST("rest/v1/completed_workouts")
    suspend fun uploadCompletedWorkout(
        @Body workout: Map<String, Any>
    ): retrofit2.Response<Unit>
}

class SupabaseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header("apikey", SupabaseConfig.apiKey)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            
        // If we have an active authorization token, attach it!
        val activeToken = SupabaseSessionManager.authToken
        if (!activeToken.isNullOrEmpty()) {
            builder.header("Authorization", "Bearer $activeToken")
        }
        
        return chain.proceed(builder.build())
    }
}

object SupabaseSessionManager {
    var authToken: String? = null
    var userEmail: String? = null
}

class SupabaseClient private constructor() {
    private val client = OkHttpClient.Builder()
        .addInterceptor(SupabaseInterceptor())
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(if (SupabaseConfig.url.endsWith("/")) SupabaseConfig.url else "${SupabaseConfig.url}/")
        .client(client)
        .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create())
        .build()

    val auth: SupabaseAuthApi = retrofit.create(SupabaseAuthApi::class.java)
    val db: SupabaseDbApi = retrofit.create(SupabaseDbApi::class.java)

    companion object {
        @Volatile
        private var INSTANCE: SupabaseClient? = null

        fun getInstance(): SupabaseClient {
            return INSTANCE ?: synchronized(this) {
                val instance = SupabaseClient()
                INSTANCE = instance
                instance
            }
        }
    }
}
