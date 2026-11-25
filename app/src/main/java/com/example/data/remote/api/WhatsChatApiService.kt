package com.example.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Retrofit contract prepared for future self-hosted Node.js + PostgreSQL backend.
 * No server URL or credentials are hardcoded here.
 */
data class LoginRequestDto(
  val username: String,
  val passwordHash: String,
)

data class RegisterRequestDto(
  val username: String,
  val passwordHash: String,
  val email: String? = null,
)

data class AuthResponseDto(
  val userId: String,
  val username: String,
  val displayName: String,
  val sessionToken: String,
)

data class UsernameAvailabilityDto(
  val username: String,
  val available: Boolean,
)

data class RemoteMessageDto(
  val messageId: String,
  val conversationId: String,
  val senderId: String,
  val receiverId: String?,
  val content: String,
  val messageType: String,
  val timestamp: Long,
)

interface WhatsChatApiService {
  @POST("api/v1/auth/login")
  suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

  @POST("api/v1/auth/register")
  suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

  @GET("api/v1/users/check-username")
  suspend fun checkUsername(@Query("username") username: String): UsernameAvailabilityDto

  @PUT("api/v1/users/me/username")
  suspend fun updateUsername(@Body body: Map<String, String>): UsernameAvailabilityDto

  @POST("api/v1/messages/sync")
  suspend fun syncMessages(@Body messages: List<RemoteMessageDto>): List<RemoteMessageDto>
}
