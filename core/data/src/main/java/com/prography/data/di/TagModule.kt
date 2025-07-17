package com.prography.data.di

import com.prography.data.datasource.local.TagLocalDataSource
import com.prography.data.datasource.local.TagLocalDataSourceImpl
import com.prography.data.repository.TagRepositoryImpl
import com.prography.domain.repository.TagRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TagModule {

    @Binds
    @Singleton
    abstract fun bindTagLocalDataSource(
        tagLocalDataSourceImpl: TagLocalDataSourceImpl
    ): TagLocalDataSource

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        tagRepositoryImpl: TagRepositoryImpl
    ): TagRepository
}