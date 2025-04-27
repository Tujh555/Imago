package io.tujh.imago.presentation.editor.components.filters.shader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberShaderFilters() = remember {
    listOf(
        MarbledTexture(),
        PaperTexture(),
        SketchingPaperTexture(),
        RisographTextrure(),
        NoiseGrainTexture(),
        NoiseGrain2Texture()
    )
}