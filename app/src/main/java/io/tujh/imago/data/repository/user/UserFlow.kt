package io.tujh.imago.data.repository.user

import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.dto.toDomain
import io.tujh.imago.data.store.Store
import io.tujh.imago.domain.user.CurrentUser
import io.tujh.imago.domain.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class UserFlow @Inject constructor(
    store: Store<UserDto>,
    scope: CoroutineScope
) : CurrentUser, StateFlow<User?> by store.toDomain(scope)

private fun Store<UserDto>.toDomain(scope: CoroutineScope) = data
    .map { it?.toDomain() }
    .stateIn(scope, SharingStarted.Lazily, null)