package io.tujh.imago.data.repository.image

import io.tujh.imago.data.store.Store
import io.tujh.imago.domain.image.draw.DrawSettings
import io.tujh.imago.domain.image.draw.DrawSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DrawRepository @Inject constructor(
    private val store: Store<DrawSettings>,
    private val scope: CoroutineScope
) : DrawSettingsRepository {
    override val current = store.data

    override fun update(settings: DrawSettings) {
        scope.launch { store.put(settings) }
    }
}