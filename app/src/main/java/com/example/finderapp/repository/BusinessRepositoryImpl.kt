package com.example.finderapp.repository

import com.example.finderapp.data.model.Business
import com.example.finderapp.data.model.BusinessesList
import com.example.finderapp.data.model.Reviews
import com.example.finderapp.data.remote.RemoteDataSource
import javax.inject.Inject

class BusinessRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource): BusinessRepository {

    override suspend fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String): BusinessesList {
        return remoteDataSource.getBusinessByKeyword(latitude, longitude, term)
    }

    override suspend fun getBusinessById(id: String): Business {
        return remoteDataSource.getBusinessById(id)
    }

    override suspend fun getReviewsByBusiness(id: String): Reviews {
        return remoteDataSource.getReviewsByBusiness(id)
    }

}