package io.tujh.imago.presentation.editor.components.filters.shader

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.IntSize
import io.tujh.imago.presentation.editor.components.filters.ImageFilter
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
abstract class ShaderFilter : ImageFilter {
    abstract val shader: RuntimeShader
    val name: String = javaClass.simpleName ?: UUID.randomUUID().toString()

    final override fun onSizeChanged(size: IntSize) {
        shader.setFloatUniform("resolution", size.width.toFloat(), size.height.toFloat())
    }

    final override fun toString(): String = name

    final override fun equals(other: Any?) = name == (other as? ShaderFilter)?.name

    final override fun hashCode(): Int  = name.hashCode()
}