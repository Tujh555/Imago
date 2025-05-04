package io.tujh.imago.presentation.components

import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat

enum class ReorderHapticFeedbackType {
    START,
    MOVE,
    END,
}

@Stable
interface ReorderHapticFeedback {
    fun performHapticFeedback(type: ReorderHapticFeedbackType)
}


@Composable
fun rememberReorderHapticFeedback(): ReorderHapticFeedback {
    val view = LocalView.current

    val reorderHapticFeedback = remember {
        object : ReorderHapticFeedback {
            override fun performHapticFeedback(type: ReorderHapticFeedbackType) {
                when (type) {
                    ReorderHapticFeedbackType.START -> view.performHapticFeedback(
                        HapticFeedbackConstantsCompat.GESTURE_START,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )

                    ReorderHapticFeedbackType.MOVE -> view.performHapticFeedback(
                        HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )

                    ReorderHapticFeedbackType.END -> view.performHapticFeedback(
                        HapticFeedbackConstantsCompat.GESTURE_END,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                }
            }
        }
    }

    return reorderHapticFeedback
}

fun ReorderHapticFeedback.start() = performHapticFeedback(ReorderHapticFeedbackType.START)

fun ReorderHapticFeedback.move() = performHapticFeedback(ReorderHapticFeedbackType.MOVE)

fun ReorderHapticFeedback.end() = performHapticFeedback(ReorderHapticFeedbackType.END)