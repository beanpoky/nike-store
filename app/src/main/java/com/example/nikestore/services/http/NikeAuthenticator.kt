package com.example.nikestore.services.http

import android.util.Log
import com.example.nikestore.data.RefreshRequest
import com.example.nikestore.data.TokenContainer
import com.example.nikestore.data.TokenResponse
import com.example.nikestore.data.repo.source.CLIENT_ID
import com.example.nikestore.data.repo.source.CLIENT_SECRET
import com.example.nikestore.data.repo.source.UserDataSource
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NikeAuthenticator : Authenticator, KoinComponent {
    private val apiService: ApiService by inject()
    private val userLocalDataSource: UserDataSource by inject()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (TokenContainer.token != null && TokenContainer.refreshToken != null && !response.request.url.pathSegments.last()
                .equals("token", false)
        ) {
            try {
                val token = "" // refreshToken()
                if (token.isEmpty())
                    return null

                return response.request.newBuilder().header("Authorization", "Bearer $token")
                    .build()

            } catch (exception: Exception) {
                Log.e("TAG", "authenticate: ${exception.message}")
            }
        }

        return null
    }

    private suspend fun refreshToken(): String {
        val response: retrofit2.Response<TokenResponse> =
            apiService.refreshToken(
                RefreshRequest(
                    grantType = "refresh_token",
                    refreshToken = TokenContainer.refreshToken!!,
                    clientId = CLIENT_ID,
                    clientSecret = CLIENT_SECRET
                )
            )
        response.body()?.let {
            TokenContainer.update(it.accessToken, it.refreshToken)
            userLocalDataSource.saveToken(it.accessToken, it.refreshToken)
            return it.accessToken
        }

        return ""
    }
}