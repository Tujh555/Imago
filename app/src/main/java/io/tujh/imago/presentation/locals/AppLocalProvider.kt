package io.tujh.imago.presentation.locals

import androidx.compose.runtime.Composable
import io.tujh.imago.domain.image.draw.DrawSettingsRepository
import javax.inject.Inject

class AppLocalProvider @Inject constructor(
    private val drawSettingsRepository: DrawSettingsRepository
) {
    @Composable
    fun WithProviders(content: @Composable () -> Unit) {

    }
}