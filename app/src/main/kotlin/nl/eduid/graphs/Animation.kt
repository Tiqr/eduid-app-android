package nl.eduid.graphs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@ExperimentalAnimationApi
@Composable
fun ExampleAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(false) }
            .apply { targetState = true },
        enter = fadeIn(initialAlpha = 0.3f),
        exit = fadeOut()
    ) {
        content()
    }
}
