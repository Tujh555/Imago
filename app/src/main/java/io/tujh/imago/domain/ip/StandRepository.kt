package io.tujh.imago.domain.ip

import kotlinx.coroutines.flow.Flow

interface StandRepository {
    val current: Flow<Stand?>

    suspend fun update(new: Stand)
}