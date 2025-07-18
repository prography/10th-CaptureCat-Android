package com.prography.data.di

import com.prography.domain.repository.ScreenshotRepository
import com.prography.domain.usecase.screenshot.GetScreenshotByIdUseCase
import com.prography.domain.usecase.screenshot.DeleteScreenshotUseCase
import com.prography.domain.usecase.screenshot.UpdateScreenshotUseCase
import com.prography.domain.usecase.screenshot.DeleteTagUseCase
import com.prography.domain.usecase.screenshot.AddTagsToScreenshotUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetScreenshotByIdUseCase(
        repository: ScreenshotRepository
    ): GetScreenshotByIdUseCase = GetScreenshotByIdUseCase(repository)

    @Provides
    fun provideDeleteScreenshotUseCase(
        repository: ScreenshotRepository
    ): DeleteScreenshotUseCase = DeleteScreenshotUseCase(repository)

    @Provides
    fun provideUpdateScreenshotUseCase(
        repository: ScreenshotRepository
    ): UpdateScreenshotUseCase = UpdateScreenshotUseCase(repository)

    @Provides
    fun provideDeleteTagUseCase(
        repository: ScreenshotRepository
    ): DeleteTagUseCase = DeleteTagUseCase(repository)

    @Provides
    fun provideAddTagsToScreenshotUseCase(
        repository: ScreenshotRepository
    ): AddTagsToScreenshotUseCase = AddTagsToScreenshotUseCase(repository)
}