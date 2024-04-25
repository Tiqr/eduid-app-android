package nl.eduid.screens.personalinfo

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.model.ConfirmedName
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.UnauthorizedException
import nl.eduid.di.model.UserDetails
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val assistant: DataAssistant,
) : ViewModel() {
    private var cachedUserDetails: UserDetails? = null
    var uiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            uiState = UiState(isLoading = true)
            try {
                cachedUserDetails = assistant.getErringUserDetails()
                uiState = cachedUserDetails?.let { details ->
                    val personalInfo = mapUserDetailsToPersonalInfo(details)
                    UiState(isLoading = false, personalInfo = personalInfo)
                } ?: UiState(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                        messageId = R.string.ResponseErrors_PersonalDetailsRetrieveError_COPY
                    )
                )
            } catch (e: UnauthorizedException) {
                uiState = uiState.copy(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                        messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                    )
                )
            }

        }
    }

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
        uiState = uiState.copy(errorData = null)
    }

    fun removeConnection(index: Int) = viewModelScope.launch {
        val details = cachedUserDetails ?: return@launch
        uiState = uiState.copy(isLoading = true)
        try {
            val linkedAccount = details.linkedAccounts[index]
            val newDetails = assistant.removeConnection(linkedAccount)
            uiState = newDetails?.let { updatedDetails ->
                cachedUserDetails = updatedDetails
                val personalInfo = mapUserDetailsToPersonalInfo(updatedDetails)
                uiState.copy(isLoading = false, personalInfo = personalInfo)
            } ?: uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_GeneralRequestError_COPY,
                )
            )
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    fun requestLinkUrl() = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true, linkUrl = null)
        try {
            val response = assistant.getStartLinkAccount()
            uiState = if (response != null) {
                uiState.copy(
                    linkUrl = createLaunchIntent(response), isLoading = false
                )
            } else {
                uiState.copy(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.ResponseErrors_GeneralRequestError_COPY
                    )
                )
            }
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    fun updateName(givenName: String, familyName: String) = viewModelScope.launch {
        val currentDetails = cachedUserDetails ?: return@launch
        uiState = uiState.copy(isLoading = true)
        try {
            val validatedSelfName =
                SelfAssertedName(familyName = givenName.ifEmpty { currentDetails.givenName },
                    givenName = familyName.ifEmpty { currentDetails.familyName })
            val newDetails = assistant.updateName(validatedSelfName)
            uiState = newDetails?.let { updatedDetails ->
                cachedUserDetails = updatedDetails
                val personalInfo = mapUserDetailsToPersonalInfo(updatedDetails)
                uiState.copy(isLoading = false, personalInfo = personalInfo)
            } ?: uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_GeneralRequestError_COPY
                )
            )
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
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
            nameStatus = PersonalInfo.InfoStatus.Final,
            email = email,
            emailStatus = PersonalInfo.InfoStatus.Editable,
            institutionAccounts = institutionAccounts,
            dateCreated = dateCreated,
        )
    }
}