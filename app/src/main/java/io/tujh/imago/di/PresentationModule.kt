package io.tujh.imago.di

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap
import io.tujh.imago.presentation.screens.edit.ImageEditScreenModel
import io.tujh.imago.presentation.screens.splash.SplashModel

@Module
@InstallIn(ActivityComponent::class)
interface PresentationModule {
    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ImageEditScreenModel.Factory::class)
    fun edit(factory: ImageEditScreenModel.Factory): ScreenModelFactory

    @Binds
    @IntoMap
    @ScreenModelKey(SplashModel::class)
    fun splash(model: SplashModel): ScreenModel
}