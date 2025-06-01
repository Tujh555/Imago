package io.tujh.imago.data

import android.util.Log
import io.tujh.imago.data.store.Store
import io.tujh.imago.domain.ip.Stand
import io.tujh.imago.domain.ip.StandRepository
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StandRepositoryImpl @Inject constructor(
    private val store: Store<Stand>
) : StandRepository {
    override val current = store.data
        .onEach { Log.d("--tag", "current = $it") }

    override suspend fun update(new: Stand) {
        store.put(new)
    }
}