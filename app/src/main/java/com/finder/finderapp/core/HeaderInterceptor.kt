package com.finder.finderapp.core

import android.content.Context
import com.finder.finderapp.R
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val context: Context): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${context.getString(R.string.YELP_API_KEY)}")
            .addHeader("accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}