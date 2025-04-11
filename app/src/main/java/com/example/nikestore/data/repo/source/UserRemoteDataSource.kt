package com.example.nikestore.data.repo.source

import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.RegisterRequest
import com.example.nikestore.data.TokenRequest
import com.example.nikestore.data.TokenResponse
import com.example.nikestore.services.http.ApiService
import retrofit2.Response

const val CLIENT_ID = 2
const val CLIENT_SECRET = "kyj1c9sVcksqGU4scMX7nLDalkjp2WoqQEf8PKAC"

class UserRemoteDataSource(private val apiService: ApiService) : UserDataSource {

    override suspend fun login(email: String, password: String): Response<TokenResponse> =
        apiService.login(
            TokenRequest(
                email = email,
                password = password,
                grantType = "password",
                clientId = CLIENT_ID,
                clientSecret = CLIENT_SECRET
            )
        )

    override suspend fun signUp(email: String, password: String): Response<MessageResponse> =
        apiService.signUp(
            RegisterRequest(email = email, password = password)
        )

    override suspend fun loadToken() {
        TODO("Not yet implemented")
    }

    override suspend fun saveToken(token: String, refreshToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveEmail(email: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getEmail(): String {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }
}