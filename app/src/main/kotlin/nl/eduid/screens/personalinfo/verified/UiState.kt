package nl.eduid.screens.personalinfo.verified

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import nl.eduid.screens.personalinfo.PersonalInfo

data class UiState(
    val personalInfo: PersonalInfo? = null,
    val accounts: ImmutableList<PersonalInfo.InstitutionAccount> = emptyList<PersonalInfo.InstitutionAccount>().toImmutableList(),
    val isLoading: Boolean = false,
)