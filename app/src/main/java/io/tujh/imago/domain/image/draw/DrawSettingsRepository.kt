package io.tujh.imago.domain.image.draw

import kotlinx.coroutines.flow.Flow

interface DrawSettingsRepository {
    val current: Flow<DrawSettings?>

    fun update(settings: DrawSettings)
}