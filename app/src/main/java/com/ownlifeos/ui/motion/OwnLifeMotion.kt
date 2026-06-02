package com.ownlifeos.ui.motion

import android.animation.ValueAnimator
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

object OwnLifeMotion {
    const val Quick = 160
    const val Standard = 240
    const val Progress = 650
}

@Composable
fun rememberMotionEnabled(): Boolean = remember {
    ValueAnimator.areAnimatorsEnabled()
}

fun ownLifeEnterTransition(motionEnabled: Boolean): EnterTransition =
    if (motionEnabled) {
        fadeIn(animationSpec = tween(OwnLifeMotion.Standard, easing = FastOutSlowInEasing)) +
            slideInVertically(
                animationSpec = tween(OwnLifeMotion.Standard, easing = FastOutSlowInEasing),
                initialOffsetY = { it / 12 }
            )
    } else {
        fadeIn(animationSpec = snap())
    }

fun ownLifeExitTransition(motionEnabled: Boolean): ExitTransition =
    if (motionEnabled) {
        fadeOut(animationSpec = tween(OwnLifeMotion.Quick, easing = FastOutSlowInEasing)) +
            slideOutVertically(
                animationSpec = tween(OwnLifeMotion.Quick, easing = FastOutSlowInEasing),
                targetOffsetY = { it / 16 }
            )
    } else {
        fadeOut(animationSpec = snap())
    }
