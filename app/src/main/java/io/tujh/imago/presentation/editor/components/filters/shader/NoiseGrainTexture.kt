package io.tujh.imago.presentation.editor.components.filters.shader

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.unit.dp
import io.tujh.imago.presentation.editor.components.crop.options.SliderSelection

class NoiseGrainTexture : ShaderFilter() {
    override val shader = RuntimeShader(
        """
    uniform float2 resolution;
    uniform shader image; 
    uniform float intensity;
    
    vec4 main( vec2 fragCoord )
    {
        vec2 uv = fragCoord/resolution.xy;
        
        if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
            return vec4(image.eval(fragCoord));
        }

        float mdf = -0.8 * intensity;
        float noise = (fract(sin(dot(uv, vec2(12.9898,78.233)*2.0)) * 43758.5453));
        vec4 tex = vec4(image.eval(fragCoord));
        
        mdf *= 1.5;
        
        vec4 col = tex - noise * mdf;

        return col;
    }
    """.trimIndent()
    )
    private var intensity by mutableFloatStateOf(0.5f)

    context(GraphicsLayerScope)
    override fun apply() {
        shader.setFloatUniform("intensity", intensity)
        renderEffect = RenderEffect
            .createRuntimeShaderEffect(shader, "image")
            .asComposeRenderEffect()
    }

    @Composable
    override fun Controls() {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            SliderSelection(
                value = intensity,
                onValueChange = { intensity = it },
                valueRange = 0f..1f
            )
        }
    }
}