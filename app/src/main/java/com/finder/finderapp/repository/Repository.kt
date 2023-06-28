package com.finder.finderapp.repository

import com.finder.finderapp.data.model.Business
import com.finder.finderapp.data.model.BusinessesList
import com.finder.finderapp.data.model.Reviews
import com.finder.finderapp.data.model.Route

interface Repository {
    suspend fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String): BusinessesList
    suspend fun getBusinessById(id: String): Business
    suspend fun getReviewsByBusiness(id: String): Reviews
    suspend fun getRouteByLocation(start: String, end: String): Route
}