package io.tujh.imago.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.store.jsonStore
import io.tujh.imago.data.store.stringStore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
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
        fun fs() = FileSystem.SYSTEM

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
    }
}