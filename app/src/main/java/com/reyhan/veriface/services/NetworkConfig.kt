package com.reyhan.veriface.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkConfig {

    private val BASE_URL = "https://1cdb-182-2-141-245.ngrok-free.app/api/" // Replace with your Spring Boot API URL

    // Set up logging and OkHttp client
    fun getRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) // Log request/response body
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)  // Add logging interceptor
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL) // Set your backend API URL
            .client(client)     // Use the custom OkHttp client
            .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Kotlin objects
            .build()
    }

    // Create the service interface for interacting with the DataController API
    fun getVerifaceService(): VerifaceServices {
        return getRetrofit().create(VerifaceServices::class.java)
    }
}
