package io.tujh.imago.domain.image.draw

data class DrawSettings(
    val size: Float,
    val selectedColor: String,
    val opacity: Float,
    val availableColors: List<String>,
    val brushFactory: String
)