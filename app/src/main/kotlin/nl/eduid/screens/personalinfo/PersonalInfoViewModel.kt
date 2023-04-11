package nl.eduid.screens.personalinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.di.model.UserDetails
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(private val repository: PersonalInfoRepository) :
    ViewModel() {
    val personalInfo = MutableLiveData<PersonalInfo>()

    init {
        viewModelScope.launch {
            val userDetails = repository.getUserDetails()
            if (userDetails != null) {
                var uiData = convertToUiData(userDetails)
                val nameMap = mutableMapOf<String, String>()
                for (account in userDetails.linkedAccounts) {
                    val mappedName = repository.getInstitutionName(account.schacHomeOrganization)
                    mappedName?.let {
                        //If name found, add to list of mapped names
                        nameMap[account.schacHomeOrganization] = mappedName
                        //Get name provider from FIRST linked account
                        if (account.schacHomeOrganization == userDetails.linkedAccounts.firstOrNull()?.schacHomeOrganization) {
                            uiData = uiData.copy(
                                nameProvider = nameMap[account.schacHomeOrganization]
                                    ?: uiData.nameProvider
                            )
                        }
                        //Update UI data to include mapped institution names
                        uiData =
                            uiData.copy(institutionAccounts = uiData.institutionAccounts.map { institution ->
                                institution.copy(
                                    roleProvider = nameMap[institution.roleProvider]
                                        ?: institution.roleProvider
                                )
                            })
                        personalInfo.postValue(uiData)
                    }
                }
                personalInfo.postValue(uiData)
            }
        }
    }

    private fun convertToUiData(userDetails: UserDetails): PersonalInfo {
        val dateCreated = userDetails.created
        val linkedAccounts = userDetails.linkedAccounts

        //Not sure if we should use the eduPersonAffiliations or the schacHomeOrganisation to get the institution name
        //val affiliation = linkedAccounts.firstOrNull()?.eduPersonAffiliations?.firstOrNull()
        //val nameProvider = affiliation?.substring(affiliation.indexOf("@"),affiliation.length) ?: "You"
        val nameProvider = linkedAccounts.firstOrNull()?.schacHomeOrganization ?: "You"
        val name: String = linkedAccounts.firstOrNull()?.let {
            "${it.givenName} ${it.familyName}"
        } ?: "${userDetails.givenName} ${userDetails.familyName}"

        val emailProvider = "You"
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
            emailProvider = emailProvider,
            emailStatus = PersonalInfo.InfoStatus.Editable,
            institutionAccounts = institutionAccounts,
            dateCreated = dateCreated,
        )
    }
}