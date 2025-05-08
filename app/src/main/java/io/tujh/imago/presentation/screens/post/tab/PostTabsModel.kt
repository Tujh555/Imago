package io.tujh.imago.presentation.screens.post.tab

import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import javax.inject.Inject

class PostTabsModel @Inject constructor() :
    StateModel<PostTabsScreen.Action, PostTabsScreen.State>,
    StateHolder<PostTabsScreen.State> by StateHolder(PostTabsScreen.State()) {
    override fun onAction(action: PostTabsScreen.Action) = Unit
}