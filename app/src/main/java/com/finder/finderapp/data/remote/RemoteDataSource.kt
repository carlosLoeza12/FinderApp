package com.finder.finderapp.data.remote

import com.finder.finderapp.BuildConfig
import com.finder.finderapp.data.model.Business
import com.finder.finderapp.data.model.BusinessesList
import com.finder.finderapp.data.model.Reviews
import com.finder.finderapp.data.model.Route
import com.finder.finderapp.repository.WebServiceOpenRoute
import com.finder.finderapp.repository.WebServiceYelp
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val webServiceYelp: WebServiceYelp,
    private val webServiceOpenRoute: WebServiceOpenRoute
) {

    suspend fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String): BusinessesList{
        return webServiceYelp.getBusinessByKeyword(latitude, longitude, term)
    }

    suspend fun getBusinessById(id: String): Business{
        return webServiceYelp.getBusinessById(id)
    }

    suspend fun getReviewsByBusiness(id: String): Reviews{
        return webServiceYelp.getReviewsByBusiness(id)
    }

    suspend fun getRouteByLocation(start: String, end: String): Route{
        return webServiceOpenRoute.getRouteByLocation(BuildConfig.OPEN_ROUTE_API_KEY, start, end)
    }
}