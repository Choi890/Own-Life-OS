package com.ownlifeos.ui.components

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.ownlifeos.ui.motion.rememberMotionEnabled

enum class FluidButtonStyle {
    Filled,
    Outlined,
    Tonal
}

enum class FluidButtonTone {
    Start,
    Confirm,
    Delay,
    Risk,
    Neutral
}

@Composable
fun FluidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: FluidButtonStyle = FluidButtonStyle.Filled,
    tone: FluidButtonTone? = null,
    icon: ImageVector? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    colors: ButtonColors? = null,
    border: BorderStroke? = null,
    expandContent: Boolean = false
) {
    val motionEnabled = rememberMotionEnabled()
    val view = LocalView.current
    val resolvedTone = tone ?: remember(text) { text.fluidTone() }
    val feedbackColor = buttonFeedbackColor(resolvedTone)
    val shape = MaterialTheme.shapes.extraLarge
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    var feedbackKey by remember { mutableIntStateOf(0) }

    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled && motionEnabled) 0.965f else 1f,
        animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        label = "button press scale"
    )
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.22f else 0f,
        animationSpec = tween(durationMillis = if (pressed) 60 else 180),
        label = "button press overlay"
    )

    val clickHandler = {
        if (enabled) {
            view.performButtonHaptic(resolvedTone)
            feedbackKey += 1
            onClick()
        }
    }

    val content: @Composable () -> Unit = {
        FeedbackButtonContent(
            text = text,
            icon = icon,
            feedbackKey = feedbackKey,
            feedbackColor = feedbackColor,
            pressAlpha = pressAlpha,
            shape = shape,
            contentPadding = contentPadding,
            motionEnabled = motionEnabled,
            expandContent = expandContent
        )
    }

    val scaledModifier = modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    when (style) {
        FluidButtonStyle.Filled -> Button(
            onClick = clickHandler,
            modifier = scaledModifier,
            enabled = enabled,
            shape = shape,
            colors = colors ?: ButtonDefaults.buttonColors(),
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp),
            content = { content() }
        )

        FluidButtonStyle.Outlined -> {
            val resolvedColors = colors ?: ButtonDefaults.outlinedButtonColors()
            if (border == null) {
                OutlinedButton(
                    onClick = clickHandler,
                    modifier = scaledModifier,
                    enabled = enabled,
                    shape = shape,
                    colors = resolvedColors,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp),
                    content = { content() }
                )
            } else {
                OutlinedButton(
                    onClick = clickHandler,
                    modifier = scaledModifier,
                    enabled = enabled,
                    shape = shape,
                    colors = resolvedColors,
                    border = border,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp),
                    content = { content() }
                )
            }
        }

        FluidButtonStyle.Tonal -> FilledTonalButton(
            onClick = clickHandler,
            modifier = scaledModifier,
            enabled = enabled,
            shape = shape,
            colors = colors ?: ButtonDefaults.filledTonalButtonColors(),
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp),
            content = { content() }
        )
    }
}

@Composable
private fun FeedbackButtonContent(
    text: String,
    icon: ImageVector?,
    feedbackKey: Int,
    feedbackColor: Color,
    pressAlpha: Float,
    shape: CornerBasedShape,
    contentPadding: PaddingValues,
    motionEnabled: Boolean,
    expandContent: Boolean
) {
    val flashProgress = remember { Animatable(1f) }

    LaunchedEffect(feedbackKey, motionEnabled) {
        if (feedbackKey > 0 && motionEnabled) {
            flashProgress.snapTo(0f)
            flashProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
            .then(if (expandContent) Modifier.fillMaxWidth() else Modifier)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            }
            Text(text)
        }

        if (pressAlpha > 0f || flashProgress.value < 1f) {
            TouchFeedbackOverlay(
                pressAlpha = pressAlpha,
                flashProgress = flashProgress.value,
                feedbackColor = feedbackColor,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TouchFeedbackOverlay(
    pressAlpha: Float,
    flashProgress: Float,
    feedbackColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (pressAlpha > 0f) {
            drawRect(color = feedbackColor.copy(alpha = pressAlpha))
        }

        if (flashProgress < 1f) {
            val eased = 1f - (1f - flashProgress) * (1f - flashProgress)
            val radius = size.maxDimension * (0.18f + eased * 0.92f)
            val alpha = (0.28f * (1f - flashProgress)).coerceIn(0f, 0.28f)
            drawCircle(
                color = feedbackColor.copy(alpha = alpha),
                radius = radius,
                center = Offset(size.width / 2f, size.height / 2f)
            )
            drawRect(color = feedbackColor.copy(alpha = alpha * 0.42f))
        }
    }
}

private fun String.fluidTone(): FluidButtonTone {
    return when {
        contains("완료") || contains("저장") || contains("확정") || contains("반영") -> FluidButtonTone.Confirm
        contains("미루") || contains("보류") || contains("줄이") || contains("삭제") -> FluidButtonTone.Delay
        contains("위험") || contains("오류") || contains("실패") -> FluidButtonTone.Risk
        contains("시작") || contains("추가") || contains("확인") || contains("전략") -> FluidButtonTone.Start
        else -> FluidButtonTone.Neutral
    }
}

@Composable
private fun buttonFeedbackColor(tone: FluidButtonTone): Color {
    val colorScheme = MaterialTheme.colorScheme
    return when (tone) {
        FluidButtonTone.Start -> colorScheme.primary
        FluidButtonTone.Confirm -> Color(0xFF2DB56F)
        FluidButtonTone.Delay -> Color(0xFFE0A928)
        FluidButtonTone.Risk -> colorScheme.error
        FluidButtonTone.Neutral -> colorScheme.secondary
    }
}

private fun android.view.View.performButtonHaptic(tone: FluidButtonTone) {
    val feedback = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && tone == FluidButtonTone.Confirm -> {
            HapticFeedbackConstants.CONFIRM
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && tone == FluidButtonTone.Delay -> {
            HapticFeedbackConstants.REJECT
        }
        tone == FluidButtonTone.Neutral -> HapticFeedbackConstants.CLOCK_TICK
        else -> HapticFeedbackConstants.VIRTUAL_KEY
    }
    performHapticFeedback(feedback)
}
