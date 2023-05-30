package com.example.kodeapp.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @GET("mocks/kode-education/trainee-test/25143926/users")
    suspend fun getUsers(): UsersList

}