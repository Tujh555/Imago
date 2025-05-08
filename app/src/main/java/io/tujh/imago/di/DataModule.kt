package io.tujh.imago.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.files.UriProvider
import io.tujh.imago.data.image.GetFullImageLoader
import io.tujh.imago.data.image.Loader
import io.tujh.imago.data.image.Saver
import io.tujh.imago.data.repository.auth.AuthRepositoryImpl
import io.tujh.imago.data.repository.image.DrawRepository
import io.tujh.imago.data.repository.post.AllSource
import io.tujh.imago.data.repository.post.FavoritesSource
import io.tujh.imago.data.repository.post.OwnSource
import io.tujh.imago.data.repository.post.PostRepositoryImpl
import io.tujh.imago.data.repository.user.UserFlow
import io.tujh.imago.data.store.jsonStore
import io.tujh.imago.data.store.stringStore
import io.tujh.imago.domain.auth.AuthRepository
import io.tujh.imago.domain.image.BitmapLoader
import io.tujh.imago.domain.image.BitmapSaver
import io.tujh.imago.domain.image.FullImageLoader
import io.tujh.imago.domain.image.WriteableUriProvider
import io.tujh.imago.domain.image.draw.DrawSettings
import io.tujh.imago.domain.image.draw.DrawSettingsRepository
import io.tujh.imago.domain.post.repository.PostRepository
import io.tujh.imago.domain.post.repository.PostSource
import io.tujh.imago.domain.user.CurrentUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun loader(impl: Loader.Factory): BitmapLoader.Factory

    @Binds
    fun user(impl: UserFlow): CurrentUser

    @Binds
    fun drawRepository(impl: DrawRepository) : DrawSettingsRepository

    @Binds
    fun auth(impl: AuthRepositoryImpl) : AuthRepository

    @Binds
    fun allPostFactory(impl: AllSource.Factory): PostSource.AllFactory

    @Binds
    fun ownPostFactory(impl: OwnSource.Factory): PostSource.OwnFactory

    @Binds
    fun favoritesPostFactory(impl: FavoritesSource.Factory): PostSource.FavoritesFactory

    @Binds
    fun postRepositoryFactory(impl: PostRepositoryImpl): PostRepository

    @Binds
    fun uriProvider(impl: UriProvider): WriteableUriProvider

    @Binds
    fun saver(impl: Saver): BitmapSaver

    companion object {
        @Provides
        @Singleton
        fun ioScope() = CoroutineScope(
            Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }
        )

        @Provides
        @Singleton
        fun userStore(@ApplicationContext context: Context) = jsonStore<UserDto>(
            context = context,
            name = "user"
        )

        @Provides
        @Singleton
        fun tokenStore(@ApplicationContext context: Context) = stringStore(
            context = context,
            name = "token"
        )

        @Provides
        @Singleton
        fun drawSettingsStore(@ApplicationContext context: Context) = jsonStore<DrawSettings>(
            context = context,
            name = "draw_settings"
        )

        @Provides
        @Singleton
        fun full(get: GetFullImageLoader) = FullImageLoader(get())
    }
}