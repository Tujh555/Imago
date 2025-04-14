package io.tujh.imago.domain.user

import kotlinx.coroutines.flow.StateFlow

interface CurrentUser : StateFlow<User?>