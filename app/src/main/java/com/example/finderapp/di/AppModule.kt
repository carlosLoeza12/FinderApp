package com.example.finderapp.di

import android.content.Context
import com.example.finderapp.application.AppConstants
import com.example.finderapp.core.HeaderInterceptor
import com.example.finderapp.core.HttpClientOpenRoute
import com.example.finderapp.core.OpenRoute
import com.example.finderapp.core.Yelp
import com.example.finderapp.repository.WebServiceOpenRoute
import com.example.finderapp.repository.WebServiceYelp
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideHttpClient(@ApplicationContext context: Context, interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HeaderInterceptor(context))
            .build()
    }

    @Provides
    @Singleton
    @Yelp
    fun provideRetrofitYelp(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL_YELP)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideWebServiceYelp(@Yelp retrofit: Retrofit): WebServiceYelp = retrofit.create(WebServiceYelp::class.java)

    @Singleton
    @Provides
    @HttpClientOpenRoute
    fun provideHttpClientOpenRoute(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }
    @Provides
    @Singleton
    @OpenRoute
    fun provideRetrofitOpenRoute(@HttpClientOpenRoute okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL_OPEN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    @Provides
    @Singleton
    fun provideWebServiceOpenRoute(@OpenRoute retrofit: Retrofit): WebServiceOpenRoute = retrofit.create(WebServiceOpenRoute::class.java)

}