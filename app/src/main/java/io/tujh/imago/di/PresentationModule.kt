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
import io.tujh.imago.presentation.screens.post.comments.CommentsModel
import io.tujh.imago.presentation.screens.post.create.PostCreateModel
import io.tujh.imago.presentation.screens.post.list.PostListModel
import io.tujh.imago.presentation.screens.post.tab.PostTabsModel
import io.tujh.imago.presentation.screens.post.view.PostViewModel
import io.tujh.imago.presentation.screens.signin.SignInModel
import io.tujh.imago.presentation.screens.signup.SignUpModel
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

    @Binds
    @IntoMap
    @ScreenModelKey(SignInModel::class)
    fun signIn(model: SignInModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(SignUpModel::class)
    fun signUp(model: SignUpModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(PostListModel.Factory::class)
    fun postList(factory: PostListModel.Factory) : ScreenModelFactory

    @Binds
    @IntoMap
    @ScreenModelKey(PostCreateModel::class)
    fun postCreate(model: PostCreateModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(PostTabsModel::class)
    fun postTabs(model: PostTabsModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(PostViewModel.Factory::class)
    fun postView(factory: PostViewModel.Factory): ScreenModelFactory

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(CommentsModel.Factory::class)
    fun postComments(factory: CommentsModel.Factory): ScreenModelFactory
}