package com.example.nikestore.data.repo

import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.TokenResponse
import retrofit2.Response

interface UserRepository {

    suspend fun login(email: String, password: String): Response<TokenResponse>

    suspend fun signUp(email: String, password: String): Response<MessageResponse>

    suspend fun loadToken()

    suspend fun getEmail(): String

    suspend fun logout()

    suspend fun onSuccessfulLogin(email: String, tokenResponse: TokenResponse)
}