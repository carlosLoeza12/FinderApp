package com.example.finderapp.repository

import com.example.finderapp.data.model.Business
import com.example.finderapp.data.model.BusinessesList
import com.example.finderapp.data.model.Reviews

interface BusinessRepository {
    suspend fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String): BusinessesList
    suspend fun getBusinessById(id: String): Business
    suspend fun getReviewsByBusiness(id: String): Reviews
}