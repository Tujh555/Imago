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
import kotlin.math.roundToInt

class SketchingPaperTexture : ShaderFilter() {
    override val shader = RuntimeShader(
        """
        uniform float2 resolution;
        uniform shader image; 
        uniform float contrast1;
        uniform float contrast2;
        uniform float amount; // 0.15

        float mod289(float x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float2 mod289(float2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float3 mod289(float3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float4 mod289(float4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        
        float permute(float x) { return mod289((34.0 * x + 1.0) * x); }
        float3 permute(float3 x) { return mod289((34.0 * x + 1.0) * x); }
        
        float4 permute(float4 x) {
          return mod289(((x * 34.0) + 1.0) * x);
        }
        
        float snoise(float2 v) {
          const float4 C = float4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
          float2 i  = floor(v + dot(v, C.yy));
          float2 x0 = v - i + dot(i, C.xx);
          float2 i1;
          i1 = (x0.x > x0.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
          float4 x12 = x0.xyxy + C.xxzz;
          x12.xy -= i1;
          i = mod289(i);
          float3 p = permute(permute(i.y + float3(0.0, i1.y, 1.0)) + i.x + float3(0.0, i1.x, 1.0));
          float3 m = max(0.5 - float3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
          m = m * m;
          m = m * m;
          float3 x = 2.0 * fract(p * C.www) - 1.0;
          float3 h = abs(x) - 0.5;
          float3 ox = floor(x + 0.5);
          float3 a0 = x - ox;
          m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);
          float3 g;
          g.x = a0.x * x0.x + h.x * x0.y;
          g.yz = a0.yz * x12.xz + h.yz * x12.yw;
          return 130.0 * dot(m, g);
        }

        half4 main( vec2 fragCoord )  {
            vec2 uv = fragCoord.xy / resolution.xy;
            if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
                return image.eval(fragCoord);
            }
            half4 baseColor = image.eval(fragCoord);
        
            // Generate Simplex noise
            float noise = snoise(uv * 200.0) * 0.5 + 0.5;
            noise = pow(noise, contrast1); // Increase contrast
        
            // Create a dot pattern
            float dotPattern = (sin(uv.x * 800.0) * sin(uv.y * 800.0)) * 0.5 + 0.5;
            dotPattern = pow(dotPattern, contrast2); // Increase contrast
        
            // Combine the noise and dot pattern
            float combinedTexture = mix(noise, dotPattern, 0.6);
        
            // Apply the texture to the base color
            half4 outputColor = baseColor + half4(combinedTexture, combinedTexture, combinedTexture, 0.0) * amount;
        
            return outputColor;
        }
        """.trimIndent()
    )
    private var amount by mutableFloatStateOf(0.4f)
    private var contrast1 by mutableFloatStateOf(5.0f)
    private var contrast2 by mutableFloatStateOf(5.0f)

    context(GraphicsLayerScope)
    override fun apply() {
        with(shader) {
            setFloatUniform("amount", amount)
            setFloatUniform("contrast1", contrast1)
            setFloatUniform("contrast2", contrast2)
        }
        renderEffect = RenderEffect
            .createRuntimeShaderEffect(shader, "image")
            .asComposeRenderEffect()
    }

    @Composable
    override fun Controls() {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            SliderSelection(
                value = contrast1,
                onValueChange = { contrast1 = it },
                valueRange = 2.0f..9.0f
            )
            Spacer(modifier = Modifier.height(16.dp))
            SliderSelection(
                value = contrast2,
                onValueChange = { contrast2 = it },
                valueRange = 2.0f..9.0f
            )
            Spacer(modifier = Modifier.height(16.dp))
            SliderSelection(
                value = amount,
                onValueChange = { amount = it },
                valueRange = 0f..1.2f
            )
        }
    }
}