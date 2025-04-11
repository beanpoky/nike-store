package com.example.nikestore.data.repo

import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.TokenContainer
import com.example.nikestore.data.TokenResponse
import com.example.nikestore.data.repo.source.UserDataSource
import retrofit2.Response

class UserRepositoryImpl(
    private val userRemoteDataSource: UserDataSource,
    private val userLocalDataSource: UserDataSource
) : UserRepository {

    override suspend fun login(email: String, password: String): Response<TokenResponse> =
        userRemoteDataSource.login(email, password)

    override suspend fun signUp(email: String, password: String): Response<MessageResponse> =
        userRemoteDataSource.signUp(email, password)

    override suspend fun loadToken() = userLocalDataSource.loadToken()

    override suspend fun getEmail(): String = userLocalDataSource.getEmail()

    override suspend fun logout() {
        userLocalDataSource.logout()
        TokenContainer.update(null, null)
    }

    override suspend fun onSuccessfulLogin(email: String, tokenResponse: TokenResponse) {
        TokenContainer.update(tokenResponse.accessToken, tokenResponse.refreshToken)
        userLocalDataSource.saveToken(tokenResponse.accessToken, tokenResponse.refreshToken)
        userLocalDataSource.saveEmail(email)
    }
}