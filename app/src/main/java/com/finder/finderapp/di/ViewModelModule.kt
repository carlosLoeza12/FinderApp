package com.finder.finderapp.di

import com.finder.finderapp.repository.Repository
import com.finder.finderapp.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindRepoImpl(repositoryImpl: RepositoryImpl): Repository
}