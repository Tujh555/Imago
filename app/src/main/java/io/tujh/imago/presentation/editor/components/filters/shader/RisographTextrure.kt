package io.tujh.imago.presentation.editor.components.filters.shader

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

class RisographTextrure : ShaderFilter() {
    override val shader = RuntimeShader(
        """
    uniform float2 resolution;
    uniform shader image; 
    uniform float randomization;
    uniform float randomizationOffset;
    
    float random( vec2 p )
    {
        vec2 K1 = vec2(
            23.14069263277926,
            2.665144142690225
        );
        return fract( cos( dot(p,K1) ) * 43758.5453 );
    }
    
    float noise( vec2 uv )
    {
      vec2 K1 = vec2(12.9898,78.233);
    	return (fract(sin(dot(uv, K1*2.0)) * 43758.5453));
    }
    
    vec4 main( vec2 fragCoord )  {
        vec2 uv = fragCoord/resolution.xy;
        
        if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
            return vec4(image.eval(fragCoord));
        }
        
        vec2 uvRandom = uv;
        float amount = 0.8;
        uvRandom.y *= noise(vec2(uvRandom.y,amount));
        vec4 tex = vec4(image.eval(fragCoord));
        vec4 originalTex = tex;
        tex.rgb += random(uvRandom) * randomization + randomizationOffset;
      
        
        float r = max(tex.r, originalTex.r);
        float g = max(tex.g, originalTex.g);
        float b = max(tex.b, originalTex.b);
        float a = 1.0;
      
        return vec4(r, g, b, a);
    }
        """.trimIndent()
    )
    private var randomization by mutableFloatStateOf(0.4f)
    private var randomizationOffset by mutableFloatStateOf(0.14f)

    context(GraphicsLayerScope)
    override fun apply() {
        with(shader) {
            setFloatUniform("randomization", randomization)
            setFloatUniform("randomizationOffset", randomizationOffset)
        }

        renderEffect = RenderEffect
            .createRuntimeShaderEffect(shader, "image")
            .asComposeRenderEffect()
    }

    @Composable
    override fun Controls() {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            SliderSelection(
                value = randomization,
                onValueChange = { randomization = it },
                valueRange = 0.07f..0.8f
            )
            Spacer(modifier = Modifier.height(16.dp))
            SliderSelection(
                value = randomizationOffset,
                onValueChange = { randomizationOffset = it },
                valueRange = 0f..1f
            )
        }
    }
}