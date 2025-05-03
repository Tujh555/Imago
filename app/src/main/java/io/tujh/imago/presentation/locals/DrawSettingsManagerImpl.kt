package io.tujh.imago.presentation.locals

import androidx.compose.ui.graphics.Color
import io.tujh.imago.domain.image.draw.DrawSettingsRepository
import io.tujh.imago.presentation.editor.components.draw.BrushFactory
import io.tujh.imago.presentation.editor.components.draw.DrawSettings
import io.tujh.imago.presentation.editor.components.draw.DrawSettingsManager
import io.tujh.imago.presentation.mappers.toColor
import io.tujh.imago.presentation.mappers.toHex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import io.tujh.imago.domain.image.draw.DrawSettings as DomainSettings

class DrawSettingsManagerImpl @Inject constructor(
    private val repository: DrawSettingsRepository
) : DrawSettingsManager {
    override val settings: Flow<DrawSettings> = repository.current.filterNotNull().map { model ->
        DrawSettings(
            size = model.size,
            selectedColor = model.selectedColor.toColor(),
            opacity = model.opacity,
            availableColors = model.availableColors.map(String::toColor),
            brushFactory = kotlin
                .runCatching { BrushFactory.valueOf(model.brushFactory) }
                .getOrDefault(BrushFactory.Basic)
        )
    }

    override fun update(settings: DrawSettings) {
        val model = DomainSettings(
            size = settings.size,
            selectedColor = settings.selectedColor.toHex(),
            opacity = settings.opacity,
            availableColors = settings.availableColors.map(Color::toHex),
            brushFactory = settings.brushFactory.name
        )
        repository.update(model)
    }
}