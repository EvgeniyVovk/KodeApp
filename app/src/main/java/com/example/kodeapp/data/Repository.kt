package com.example.kodeapp.data

import android.util.Log
import javax.inject.Inject

interface Repository {
    suspend fun getData(): Result<List<User>>
}

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService): Repository {

    override suspend fun getData(): Result<List<User>> {
        return try {
            val result = apiService.getUsers()
            Result.Success(result.items)
        } catch (e: Exception){
            Result.Error(message = e.message, e)
        }
    }
}