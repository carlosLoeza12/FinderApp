package com.example.finderapp.di

import com.example.finderapp.repository.BusinessRepository
import com.example.finderapp.repository.BusinessRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindRepoImpl(businessRepositoryImpl: BusinessRepositoryImpl): BusinessRepository
}