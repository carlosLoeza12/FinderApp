package com.finder.finderapp.repository

import com.finder.finderapp.data.model.Business
import com.finder.finderapp.data.model.BusinessesList
import com.finder.finderapp.data.model.Reviews
import com.finder.finderapp.data.model.Route
import com.finder.finderapp.data.remote.RemoteDataSource
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource): Repository {

    override suspend fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String): BusinessesList {
        return remoteDataSource.getBusinessByKeyword(latitude, longitude, term)
    }

    override suspend fun getBusinessById(id: String): Business {
        return remoteDataSource.getBusinessById(id)
    }

    override suspend fun getReviewsByBusiness(id: String): Reviews {
        return remoteDataSource.getReviewsByBusiness(id)
    }

    override suspend fun getRouteByLocation(start: String, end: String): Route {
        return remoteDataSource.getRouteByLocation(start, end)
    }

}