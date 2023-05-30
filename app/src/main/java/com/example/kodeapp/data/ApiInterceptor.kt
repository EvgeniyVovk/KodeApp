package com.example.kodeapp.data

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Prefer", "code=200")
            .addHeader("Accept", "application/json, application/xml")
            .build()
        return chain.proceed(request)
    }
}