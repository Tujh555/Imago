package io.tujh.imago.presentation.editor.components.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.tujh.imago.presentation.editor.components.draw.brush.AirbrushBrush
import io.tujh.imago.presentation.editor.components.draw.brush.BasicBrush
import io.tujh.imago.presentation.editor.components.draw.brush.BlurBrush
import io.tujh.imago.presentation.editor.components.draw.brush.DrawBrush
import io.tujh.imago.presentation.editor.components.draw.brush.EraserBrush
import io.tujh.imago.presentation.editor.components.draw.brush.GlowBrush
import io.tujh.imago.presentation.editor.components.draw.brush.MarkerBrush
import io.tujh.imago.presentation.editor.components.draw.brush.ParticleBrush
import io.tujh.imago.presentation.editor.components.draw.brush.PencilBrush

enum class BrushFactory {
    Basic {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = BasicBrush(
            startPosition = startPosition,
            width = size,
            brushColor = color,
            opacity = opacity
        )
    },
    Glow {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = GlowBrush(
            startPosition = startPosition,
            width = size,
            glowColor = color
        )
    },
    Blur {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = BlurBrush(
            startPosition = startPosition,
            size = size,
            brushColor = color
        )
    },
    Airbrush {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = AirbrushBrush(
            size = size,
            brushColor = color,
            opacity = opacity,
            startPosition = startPosition
        )
    },
    Marker {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = MarkerBrush(
            startPosition = startPosition,
            width = size,
            brushColor = color
        )
    },
    Particle {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = ParticleBrush(
            startPosition = startPosition,
            size = size,
            brushColor = color,
            opacity = opacity
        )
    },
    Pencil {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = PencilBrush(
            startPosition = startPosition,
            width = size,
            brushColor = color,
            opacity = opacity
        )
    },
    Eraser {
        override fun invoke(
            startPosition: Offset,
            size: Float,
            color: Color,
            opacity: Float
        ): DrawBrush = EraserBrush(
            startPosition = startPosition,
            width = size
        )
    };

    abstract operator fun invoke(
        startPosition: Offset,
        size: Float,
        color: Color,
        opacity: Float
    ): DrawBrush
}