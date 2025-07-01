package com.prography.domain.repository

interface AuthRepository {
    suspend fun getAuthToken(): String?
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
    suspend fun isLoggedIn(): Boolean
}