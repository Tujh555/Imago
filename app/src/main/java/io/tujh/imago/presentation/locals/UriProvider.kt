package io.tujh.imago.presentation.locals

import androidx.compose.runtime.compositionLocalOf
import io.tujh.imago.domain.image.WriteableUriProvider

val LocalUriProvider = compositionLocalOf<WriteableUriProvider?> { null }