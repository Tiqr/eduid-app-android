package nl.eduid.splash

import androidx.compose.foundation.interaction.DragInteraction

sealed class Startup {
    object Unknown : Startup()
    object RegistrationRequired : Startup()

    object AppReady : Startup()
}