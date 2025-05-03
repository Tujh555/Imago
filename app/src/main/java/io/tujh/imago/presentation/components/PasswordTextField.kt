package io.tujh.imago.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import io.tujh.imago.R

private val passwordVisualTransformation = VisualTransformation { original ->
    val text = AnnotatedString("●".repeat(original.length))
    TransformedText(text, OffsetMapping.Identity)
}
private val keyPath = arrayOf("**")

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    text: String,
    colors: TextFieldColors,
    shape: Shape,
    error: Boolean = false,
    placeholder: String = "Password",
    onValueChange: (String) -> Unit
) {
    var inputVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = onValueChange,
        isError = error,
        singleLine = true,
        label = { Text(text = placeholder) },
        visualTransformation = if (inputVisible) {
            VisualTransformation.None
        } else {
            passwordVisualTransformation
        },
        colors = colors,
        trailingIcon = {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_eye))
            val progress = remember { Animatable(0.5f) }

            LaunchedEffect(inputVisible) {
                progress.animateTo(
                    targetValue = if (inputVisible) 0f else 0.5f,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = LinearEasing
                    )
                )
            }
            val dynamicProperties = rememberLottieDynamicProperties(
                rememberLottieDynamicProperty(
                    property = LottieProperty.COLOR_FILTER,
                    value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        MaterialTheme.colorScheme.onSurface.hashCode(),
                        BlendModeCompat.SRC_ATOP
                    ),
                    keyPath = keyPath
                )
            )

            LottieAnimation(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { inputVisible = inputVisible.not() }
                    ),
                composition = composition,
                progress = { progress.value },
                dynamicProperties = dynamicProperties
            )
        },
        shape = shape
    )
}