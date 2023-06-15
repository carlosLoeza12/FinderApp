package com.example.finderapp.data.remote

import com.example.finderapp.data.model.Business
import com.example.finderapp.data.model.BusinessesList
import com.example.finderapp.data.model.Reviews
import com.example.finderapp.repository.WebService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val webService: WebService) {

    suspend fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String): BusinessesList{
        return webService.getBusinessByKeyword(latitude, longitude, term)
    }

    suspend fun getBusinessById(id: String): Business{
        return webService.getBusinessById(id)
    }

    suspend fun getReviewsByBusiness(id: String): Reviews{
        return webService.getReviewsByBusiness(id)
    }
}