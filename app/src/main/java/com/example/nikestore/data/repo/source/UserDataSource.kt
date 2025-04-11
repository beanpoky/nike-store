package com.example.nikestore.data.repo.source

import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.TokenResponse
import retrofit2.Response

interface UserDataSource {

    suspend fun login(email: String, password: String): Response<TokenResponse>

    suspend fun signUp(email: String, password: String): Response<MessageResponse>

    suspend fun loadToken()

    suspend fun saveToken(token: String, refreshToken: String)

    suspend fun saveEmail(email: String)

    suspend fun getEmail(): String

    suspend fun logout()
}