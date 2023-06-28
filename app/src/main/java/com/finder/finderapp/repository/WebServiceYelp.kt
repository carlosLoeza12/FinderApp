package com.finder.finderapp.repository

import com.finder.finderapp.data.model.Business
import com.finder.finderapp.data.model.BusinessesList
import com.finder.finderapp.data.model.Reviews
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebServiceYelp {
    @GET("businesses/search")
    suspend fun getBusinessByKeyword(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("term") term: String
    ): BusinessesList

    @GET("businesses/{id}")
    suspend fun getBusinessById(@Path("id")id : String): Business

    @GET("businesses/{id}/reviews")
    suspend fun getReviewsByBusiness(@Path("id")id : String): Reviews
}