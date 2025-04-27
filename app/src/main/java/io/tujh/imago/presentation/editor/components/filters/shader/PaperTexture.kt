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

class PaperTexture : ShaderFilter() {
    override val shader = RuntimeShader(
        """
    uniform float2 resolution;
    uniform shader image; 
    uniform float grainIntensity;
    uniform float fiberIntensity;

    vec4 noise2(vec2 uv) {
      vec4 n = vec4(fract(sin(dot(uv.xy, vec2(12.9894234,78.23342343))) * 43758.5453));
      return vec4(n.x, n.y, n.z, n.w);
    }
    
    vec4 main( vec2 fragCoord )  {
      vec2 uv = fragCoord.xy / resolution.xy;
      if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
          return vec4(image.eval(fragCoord));
      }
      
      vec4 grain = vec4(noise2(uv * 12.0).r - 0.5);
      vec4 fiber = vec4(noise2(uv * 23.0).g - 0.5);
      vec4 dots = vec4(noise2(uv * 30.0).b - 0.5);
      
      vec4 randomSpecs = vec4(0.0, 0.0, 0.0, 0.0);
      if (fract(dots.x * 10.0) > 0.8) {
        randomSpecs = vec4(0.1, 0.1, 0.1, 1.0);
      }
      
      vec4 randomFibers = vec4(0.0, 0.0, 0.0, 0.0);
      if (fract(fiber.y * 10.0 + uv.y * 10.0) > 0.95) {
        randomFibers = vec4(0.2, 0.2, 0.2, 1.0);
      }
    
      float fiberThickness = fract(fiber.y * 20.0 + uv.y * 10.0) * 0.05 + 0.01;
      vec4 squigglyFibers = vec4(1.0, 1.0, 1.0, 0.0);
      if (fract(fiber.y * 40.0 + uv.y * 20.0) > 0.95) {
        squigglyFibers = vec4(1.0, 1.0, 1.0, fiberThickness);
      }
      
      return min(vec4(image.eval(fragCoord)) 
        + grain * grainIntensity
        + fiber * fiberIntensity
        + randomSpecs, squigglyFibers);
    }
""".trimIndent()
    )
    private var grain by mutableFloatStateOf(0.05f)
    private var fiber by mutableFloatStateOf(0.5f)

    context(GraphicsLayerScope)
    override fun apply() {
        with(shader) {
            setFloatUniform("grainIntensity", grain)
            setFloatUniform("fiberIntensity", fiber)
        }
        renderEffect = RenderEffect
            .createRuntimeShaderEffect(shader, "image")
            .asComposeRenderEffect()
    }

    @Composable
    override fun Controls() {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            SliderSelection(
                value = grain,
                onValueChange = { grain = it },
                valueRange = 0f..1f
            )
            Spacer(modifier = Modifier.height(16.dp))
            SliderSelection(
                value = fiber,
                onValueChange = { fiber = it },
                valueRange = 0f..1f
            )
        }
    }
}