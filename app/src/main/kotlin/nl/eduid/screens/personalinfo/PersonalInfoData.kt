package nl.eduid.screens.personalinfo

import android.app.Person
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.model.ConfirmedName
import nl.eduid.di.model.LinkedAccountUpdateRequest
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.UserDetails
import nl.eduid.di.model.mapToPersonalInfo
import java.time.LocalDate

@Stable
data class PersonalInfo(
    val name: String = "",
    val selfAssertedName: SelfAssertedName = SelfAssertedName(),
    val confirmedName: ConfirmedName = ConfirmedName(),
    val nameProvider: String? = null,
    val email: String = "",
    val dateOfBirth: LocalDate? = null,
    val linkedInternalAccounts: ImmutableList<InstitutionAccount> = emptyList<InstitutionAccount>().toImmutableList(),
    val linkedExternalAccounts: ImmutableList<InstitutionAccount> = emptyList<InstitutionAccount>().toImmutableList(),
    val dateCreated: Long = 0,
) {
    val isVerified = linkedInternalAccounts.isNotEmpty() || linkedExternalAccounts.isNotEmpty()


    data class InstitutionAccount(
        val subjectId: String,
        val role: String?,
        val roleProvider: String?,
        val institution: String,
        val affiliationString: String? = null,
        val givenName: String? = null,
        val familyName: String? = null,
        val dateOfBirth: LocalDate? = null,
        val createdStamp: Long,
        val expiryStamp: Long,
        val updateRequest: LinkedAccountUpdateRequest
    )

    companion object {
        suspend fun fromUserDetails(userDetails: UserDetails, assistant: DataAssistant): PersonalInfo {
            var personalInfo = userDetails.mapToPersonalInfo()
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
                        personalInfo.copy(linkedInternalAccounts = personalInfo.linkedInternalAccounts.map { institution ->
                            institution.copy(
                                roleProvider = nameMap[institution.roleProvider]
                                    ?: institution.roleProvider
                            )
                        }.toImmutableList())
                }
            }
            return personalInfo

        }

        fun demoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
                selfAssertedName = SelfAssertedName("Pratchett", "Terence David John", "Terry"),
                confirmedName = ConfirmedName(),
                nameProvider = "Universiteit van Amsterdam",
                email = "r.v.hamersdonksveer@uva.nl"
            )
        }

        fun verifiedDemoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
                selfAssertedName = SelfAssertedName("Pratchett", "Terence David John", "Terry"),
                confirmedName = ConfirmedName(
                    familyName = "Pratchett",
                    familyNameConfirmedBy = "1",
                    givenName = "Terence David John",
                    givenNameConfirmedBy = "1"
                ),
                nameProvider = "Universiteit van Amsterdam",
                email = "r.v.hamersdonksveer@uva.nl",
                linkedInternalAccounts = generateInstitutionAccountList(),
            )
        }

        fun generateInstitutionAccountList() = listOf(
            InstitutionAccount(
                subjectId = "1",
                role = "Librarian",
                roleProvider = "Library",
                institution = "Unseen University",
                affiliationString = "Librarian",
                givenName = "Horace",
                familyName = "Worblehat",
                createdStamp = System.currentTimeMillis(),
                expiryStamp = System.currentTimeMillis(),
                updateRequest = LinkedAccountUpdateRequest("1", "1", false, null)
            )
        ).toImmutableList()
    }

}