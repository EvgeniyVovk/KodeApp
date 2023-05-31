package com.example.kodeapp.data

import com.example.kodeapp.data.model.UsersList
import retrofit2.http.GET

interface ApiService {
    @GET("mocks/kode-education/trainee-test/25143926/users")
    suspend fun getUsers(): UsersList

}