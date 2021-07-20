package com.raywenderlich.android.taskie.networking

import com.raywenderlich.android.taskie.App
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


private const val HEADER_AUTHORIZATION = "Authorization"

fun buildClient(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .addInterceptor(buildAuthorizationInterceptor())
        .build()

fun buildAuthorizationInterceptor() = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (App.getToken().isBlank()) return chain.proceed(originalRequest)

        val newRequest = originalRequest.newBuilder()
            .addHeader(HEADER_AUTHORIZATION, App.getToken())
            .build()

        return chain.proceed(newRequest)
    }

}


fun buildRetrofit(): Retrofit {
    return Retrofit.Builder()
        .client(buildClient())
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create().asLenient())
        .build()
}

fun buildApiService(): RemoteApiService =
    buildRetrofit().create(RemoteApiService::class.java)