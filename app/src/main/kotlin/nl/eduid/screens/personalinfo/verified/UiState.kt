package nl.eduid.screens.personalinfo.verified

import nl.eduid.screens.personalinfo.PersonalInfo

data class UiState(
    val accounts: List<PersonalInfo.InstitutionAccount> = emptyList(),
    val isLoading: Boolean = false,
)