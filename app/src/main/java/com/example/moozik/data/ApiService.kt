package com.example.moozik.data

import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ApiProductDto>
}


