package com.example.nikestore.data.repo.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.TokenContainer
import com.example.nikestore.data.TokenResponse
import kotlinx.coroutines.flow.first
import retrofit2.Response

class UserLocalDataSource(private val dataStore: DataStore<Preferences>) : UserDataSource {

    override suspend fun login(email: String, password: String): Response<TokenResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(email: String, password: String): Response<MessageResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun loadToken() {
        val preferences = dataStore.data.first()
        TokenContainer.update(
            preferences[ACCESS_TOKEN],
            preferences[REFRESH_TOKEN]
        )
    }

    override suspend fun saveToken(token: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun saveEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL] = email
        }
    }

    override suspend fun getEmail(): String {
        val preferences = dataStore.data.first()
        return preferences[EMAIL] ?: ""
    }

    override suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val EMAIL = stringPreferencesKey("email")
    }
}