package io.tujh.imago.presentation.editor.components.filters

import io.tujh.imago.presentation.editor.components.filters.shader.MarbledTexture
import io.tujh.imago.presentation.editor.components.filters.shader.ShaderFilter

enum class FilterFactory(val clazz: Class<out ShaderFilter>, val new: () -> ShaderFilter) {
    Marbled(MarbledTexture::class.java, ::MarbledTexture),
}