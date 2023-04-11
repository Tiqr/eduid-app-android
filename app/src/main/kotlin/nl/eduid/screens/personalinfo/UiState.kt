package nl.eduid.screens.personalinfo

import nl.eduid.ErrorData

data class UiState(
    val personalInfo: PersonalInfo = PersonalInfo(),
    val isLoading: Boolean = false,
    val errorData: ErrorData? = null,
)