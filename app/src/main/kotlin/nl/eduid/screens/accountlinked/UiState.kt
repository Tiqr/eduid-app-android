package nl.eduid.screens.accountlinked

import androidx.compose.runtime.Stable
import nl.eduid.screens.personalinfo.PersonalInfo

@Stable
data class UiState(
    val personalInfo: PersonalInfo = PersonalInfo(),
    val isLoading: Boolean = false
)