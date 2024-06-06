package nl.eduid.screens.personalinfo

import androidx.compose.runtime.Stable
import nl.eduid.di.model.ConfirmedName
import nl.eduid.di.model.SelfAssertedName

@Stable
data class PersonalInfo(
    val name: String = "",
    val seflAssertedName: SelfAssertedName = SelfAssertedName(),
    val confirmedName: ConfirmedName = ConfirmedName(),
    val nameProvider: String? = null,
    val email: String = "",
    val institutionAccounts: List<InstitutionAccount> = emptyList(),
    val dateCreated: Long = 0,
) {
    val isVerified = institutionAccounts.isNotEmpty()

    data class InstitutionAccount(
        val id: String,
        val linkedAccountJson: String,
        val role: String,
        val roleProvider: String,
        val institution: String,
        val affiliationString: String,
        val givenName: String? = null,
        val familyName: String? = null,
        val createdStamp: Long,
        val expiryStamp: Long,
    )

    companion object {
        fun demoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
                seflAssertedName = SelfAssertedName("Pratchett", "Terence David John", "Terry"),
                confirmedName = ConfirmedName(),
                nameProvider = "Universiteit van Amsterdam",
                email = "r.v.hamersdonksveer@uva.nl",
                institutionAccounts = emptyList(),
            )
        }

        fun verifiedDemoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
                seflAssertedName = SelfAssertedName("Pratchett", "Terence David John", "Terry"),
                confirmedName = ConfirmedName(
                    familyName = "Pratchett",
                    familyNameConfirmedBy = "1",
                    givenName = "Terence David John",
                    givenNameConfirmedBy = "1"
                ),
                nameProvider = "Universiteit van Amsterdam",
                email = "r.v.hamersdonksveer@uva.nl",
                institutionAccounts = generateInstitutionAccountList(),
            )
        }

        fun generateInstitutionAccountList() = listOf(
            InstitutionAccount(
                id = "1",
                linkedAccountJson = "{'id': '1', 'type': 'institution', 'name': 'Unseen University'}",
                role = "Librarian",
                roleProvider = "Library",
                institution = "Unseen University",
                affiliationString = "Librarian",
                givenName = "Horace",
                familyName = "Worblehat",
                createdStamp = System.currentTimeMillis(),
                expiryStamp = System.currentTimeMillis()
            )
        )
    }

}