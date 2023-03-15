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
                val uidata = convertToUiData(userDetails)
                personalInfo.postValue(uidata)
            }
        }
    }

    private fun convertToUiData(userDetails: UserDetails): PersonalInfo {
        val linkedAccounts = userDetails.linkedAccounts
        val affiliation = linkedAccounts.firstOrNull()?.eduPersonAffiliations?.firstOrNull()

        val nameProvider = affiliation?.substring(affiliation.indexOf("@"),affiliation.length) ?: "You"
        val name: String = linkedAccounts.firstOrNull()?.let {
            "${it.givenName} ${it.familyName}"
        } ?: "${userDetails.givenName} ${userDetails.familyName}"

        val emailProvider = "You"
        val email: String = userDetails.email

        val institutionAccounts = linkedAccounts.mapNotNull {account ->
            account.eduPersonAffiliations.firstOrNull()?.let {affiliation ->
                PersonalInfo.Companion.InstitutionAccount(
                    role = affiliation.substring(0,affiliation.indexOf("@")),
                    roleProvider = affiliation.substring(affiliation.indexOf("@")+1, affiliation.length),
                    institution = affiliation.substring(affiliation.indexOf("@")+1, affiliation.length),
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
        )
    }
}