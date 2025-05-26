package io.tujh.imago.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.NotAuthorizedHandler
import io.tujh.imago.presentation.MainActivity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UtilsModule {
    @Binds
    @Singleton
    fun handler(impl: MainActivity.Handler): ErrorHandler

    @Binds
    @Singleton
    fun unauthorized(impl: MainActivity.UnauthorizedHandler): NotAuthorizedHandler
}