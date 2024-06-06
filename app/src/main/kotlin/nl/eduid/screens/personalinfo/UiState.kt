package nl.eduid.screens.personalinfo

import androidx.compose.runtime.Stable

@Stable
data class UiState(
    val personalInfo: PersonalInfo = PersonalInfo(),
    val isLoading: Boolean = false,
)