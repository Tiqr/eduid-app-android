package nl.eduid.screens.personalinfo

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.SaveableResult
import nl.eduid.di.model.ConfirmedName
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.UnauthorizedException
import nl.eduid.di.model.UserDetails
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val assistant: DataAssistant,
) : ViewModel() {
    private val stateFromNetwork: Flow<UiState> = assistant.observableDetails.map { it ->
        when (it) {
            is SaveableResult.Success -> {
                val personalInfo = mapUserDetailsToPersonalInfo(it.data)
                UiState(isLoading = false, personalInfo = personalInfo)
            }

            is SaveableResult.LoadError -> if (it.exception is UnauthorizedException) {
                UiState(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                        messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                    )
                )
            } else {
                UiState(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                        messageId = R.string.ResponseErrors_PersonalDetailsRetrieveError_COPY
                    )
                )
            }

            null -> UiState(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                    messageId = R.string.ResponseErrors_PersonalDetailsRetrieveError_COPY
                )
            )
        }

    }

    private val uiStateInternal: MutableStateFlow<UiState> =
        MutableStateFlow(UiState(isLoading = true))

    val uiState =
        flowOf(stateFromNetwork, uiStateInternal).flattenConcat().distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(3_000),
            initialValue = UiState(isLoading = true),
        )

    private suspend fun mapUserDetailsToPersonalInfo(userDetails: UserDetails): PersonalInfo {
        var personalInfo = convertToUiData(userDetails)
        val nameMap = mutableMapOf<String, String>()
        for (account in userDetails.linkedAccounts) {
            val mappedName = assistant.getInstitutionName(account.schacHomeOrganization)
            mappedName?.let {
                //If name found, add to list of mapped names
                nameMap[account.schacHomeOrganization] = mappedName
                //Get name provider from FIRST linked account
                if (account.schacHomeOrganization == userDetails.linkedAccounts.firstOrNull()?.schacHomeOrganization) {
                    personalInfo = personalInfo.copy(
                        nameProvider = nameMap[account.schacHomeOrganization]
                            ?: personalInfo.nameProvider
                    )
                }
                //Update UI data to include mapped institution names
                personalInfo =
                    personalInfo.copy(institutionAccounts = personalInfo.institutionAccounts.map { institution ->
                        institution.copy(
                            roleProvider = nameMap[institution.roleProvider]
                                ?: institution.roleProvider
                        )
                    })
            }
        }
        return personalInfo
    }

    fun clearErrorData() {
        uiStateInternal.value = uiState.value.copy(errorData = null)
    }

    fun removeConnection(index: Int) = viewModelScope.launch {
        val details = assistant.observableDetails.map { it.mapToFirst() }.first()
        if (details == null) return@launch

        uiStateInternal.value = uiState.value.copy(isLoading = true)
        try {
            val linkedAccount = details.linkedAccounts[index]
            val newDetails = assistant.removeConnection(linkedAccount)
            uiStateInternal.value = newDetails?.let { updatedDetails ->
                val personalInfo = mapUserDetailsToPersonalInfo(updatedDetails)
                uiState.value.copy(isLoading = false, personalInfo = personalInfo)
            } ?: uiState.value.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_GeneralRequestError_COPY,
                )
            )
        } catch (e: UnauthorizedException) {
            uiStateInternal.value = uiState.value.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    fun requestLinkUrl() = viewModelScope.launch {
        uiStateInternal.value = uiState.value.copy(isLoading = true, linkUrl = null)
        try {
            val response = assistant.getStartLinkAccount()
            uiStateInternal.value = if (response != null) {
                uiState.value.copy(
                    linkUrl = createLaunchIntent(response), isLoading = false
                )
            } else {
                uiState.value.copy(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.ResponseErrors_GeneralRequestError_COPY
                    )
                )
            }
        } catch (e: UnauthorizedException) {
            uiStateInternal.value = uiState.value.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    private fun createLaunchIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }

    private fun convertToUiData(userDetails: UserDetails): PersonalInfo {
        val dateCreated = userDetails.created * 1000
        val linkedAccounts = userDetails.linkedAccounts

        val familyNameConfirmer = linkedAccounts.firstOrNull { it.familyName != null }
        val givenNameConfirmer = linkedAccounts.firstOrNull { it.givenName != null }

        val affiliationProvider = linkedAccounts.firstOrNull()
        val nameProvider = affiliationProvider?.schacHomeOrganization
        val name: String = affiliationProvider?.let {
            "${it.givenName} ${it.familyName}"
        } ?: "${userDetails.chosenName} ${userDetails.familyName}"

        val email: String = userDetails.email

        val institutionAccounts = linkedAccounts.mapNotNull { account ->
            account.eduPersonAffiliations.firstOrNull()?.let { affiliation ->
                //Just in case affiliation is not in the email format
                val role = if (affiliation.indexOf("@") > 0) {
                    affiliation.substring(0, affiliation.indexOf("@"))
                } else {
                    affiliation
                }
                PersonalInfo.InstitutionAccount(
                    id = account.institutionIdentifier,
                    role = role,
                    roleProvider = account.schacHomeOrganization,
                    institution = account.schacHomeOrganization,
                    affiliationString = affiliation,
                    createdStamp = account.createdAt,
                    expiryStamp = account.expiresAt,
                )
            }
        }

        return PersonalInfo(
            name = name,
            seflAssertedName = SelfAssertedName(
                familyName = userDetails.familyName,
                givenName = userDetails.givenName,
                chosenName = userDetails.chosenName
            ),
            confirmedName = ConfirmedName(
                familyName = familyNameConfirmer?.familyName,
                familyNameConfirmedBy = familyNameConfirmer?.institutionIdentifier,
                givenName = givenNameConfirmer?.givenName,
                givenNameConfirmedBy = givenNameConfirmer?.institutionIdentifier
            ),
            nameProvider = nameProvider,
            email = email,
            institutionAccounts = institutionAccounts,
            dateCreated = dateCreated,
        )
    }
}

fun SaveableResult<UserDetails>?.mapToFirst(): UserDetails? = when (this) {
    is SaveableResult.Success -> this.data
    is SaveableResult.LoadError -> null
    else -> null
}
