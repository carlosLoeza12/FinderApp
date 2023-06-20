package com.example.finderapp.repository

import com.example.finderapp.data.model.Business
import com.example.finderapp.data.model.BusinessesList
import com.example.finderapp.data.model.Reviews
import com.example.finderapp.data.model.Route

interface Repository {
    suspend fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String): BusinessesList
    suspend fun getBusinessById(id: String): Business
    suspend fun getReviewsByBusiness(id: String): Reviews
    suspend fun getRouteByLocation(start: String, end: String): Route
}