package io.tujh.imago.presentation.screens.splash

import cafe.adriel.voyager.core.model.screenModelScope
import io.tujh.imago.domain.user.CurrentUser
import io.tujh.imago.domain.utils.withMinDelay
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class SplashModel @Inject constructor(
    private val currentUser: CurrentUser
) : StateModel<Nothing, Boolean?>,
    StateHolder<Boolean?> by StateHolder(null) {

    init {
        loadUser()
    }

    override fun onAction(action: Nothing) = Unit

    private fun loadUser() {
        screenModelScope.io {
            val user = withMinDelay(1200) {
                withTimeoutOrNull(1000) {
                    currentUser.filterNotNull().first()
                }
            }
            update { user != null }
        }
    }
}