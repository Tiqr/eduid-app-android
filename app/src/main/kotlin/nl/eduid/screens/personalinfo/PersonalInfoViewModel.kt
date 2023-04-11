package nl.eduid.screens.personalinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.di.model.UserDetails
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    private var cachedUserDetails: UserDetails? = null
    val uiState = MutableLiveData<UiState>()

    init {
        viewModelScope.launch {
            uiState.postValue(UiState(isLoading = true))
            cachedUserDetails = repository.getUserDetails()
            cachedUserDetails?.let { details ->
                val personalInfo = mapUserDetailsToPersonalInfo(details)
                uiState.postValue(UiState(isLoading = false, personalInfo = personalInfo))
            } ?: uiState.postValue(
                UiState(
                    isLoading = false, errorData = ErrorData(
                        "Failed to load data", "Could not load personal details."
                    )
                )
            )
        }
    }

    private suspend fun mapUserDetailsToPersonalInfo(userDetails: UserDetails): PersonalInfo {
        var personalInfo = convertToUiData(userDetails)
        val nameMap = mutableMapOf<String, String>()
        for (account in userDetails.linkedAccounts) {
            val mappedName = repository.getInstitutionName(account.schacHomeOrganization)
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
        uiState.value = uiState.value?.copy(errorData = null)
    }

    fun removeConnection(index: Int) = viewModelScope.launch {
        val details = cachedUserDetails ?: return@launch
        val currentUiState = uiState.value ?: UiState()
        uiState.postValue(currentUiState.copy(isLoading = true))
        val linkedAccount = details.linkedAccounts[index]
        val newDetails = repository.removeConnection(linkedAccount)
        newDetails?.let { updatedDetails ->
            cachedUserDetails = updatedDetails
            val personalInfo = mapUserDetailsToPersonalInfo(updatedDetails)
            uiState.postValue(currentUiState.copy(isLoading = false, personalInfo = personalInfo))
        } ?: uiState.postValue(
            currentUiState.copy(
                isLoading = false,
                errorData = ErrorData("Failed to remove connection", "Could not remove connection")
            )
        )
    }

    private fun convertToUiData(userDetails: UserDetails): PersonalInfo {
        val dateCreated = userDetails.created
        val linkedAccounts = userDetails.linkedAccounts

        //Not sure if we should use the eduPersonAffiliations or the schacHomeOrganisation to get the institution name
        //val affiliation = linkedAccounts.firstOrNull()?.eduPersonAffiliations?.firstOrNull()
        //val nameProvider = affiliation?.substring(affiliation.indexOf("@"),affiliation.length) ?: "You"
        val nameProvider = linkedAccounts.firstOrNull()?.schacHomeOrganization
        val name: String = linkedAccounts.firstOrNull()?.let {
            "${it.givenName} ${it.familyName}"
        } ?: "${userDetails.givenName} ${userDetails.familyName}"

        val email: String = userDetails.email

        val institutionAccounts = linkedAccounts.mapNotNull { account ->
            account.eduPersonAffiliations.firstOrNull()?.let { affiliation ->
                //Just in case affiliation is not in the email format
                val role = if (affiliation.indexOf("@") > 0) {
                    affiliation.substring(0, affiliation.indexOf("@"))
                } else {
                    affiliation
                }
                PersonalInfo.Companion.InstitutionAccount(
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
            nameProvider = nameProvider,
            nameStatus = PersonalInfo.InfoStatus.Final,
            email = email,
            emailStatus = PersonalInfo.InfoStatus.Editable,
            institutionAccounts = institutionAccounts,
            dateCreated = dateCreated,
        )
    }
}