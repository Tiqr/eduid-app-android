package nl.eduid.screens.personalinfo.verified

import nl.eduid.screens.personalinfo.PersonalInfo

data class UiState(
    val verifier: PersonalInfo.InstitutionAccount? = null,
    val isLoading: Boolean = false,
)