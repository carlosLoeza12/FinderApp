package com.example.finderapp.repository

import com.example.finderapp.data.model.Route
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServiceOpenRoute {

    @GET("directions/driving-car")
    suspend fun getRouteByLocation(
        @Query("api_key") apiKey: String,
        @Query("start", encoded = true) start: String,
        @Query("end", encoded = true) end: String,
    ):Route
}