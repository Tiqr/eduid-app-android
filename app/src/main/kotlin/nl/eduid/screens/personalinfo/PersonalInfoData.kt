package nl.eduid.screens.personalinfo

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import nl.eduid.di.model.ConfirmedName
import nl.eduid.di.model.SelfAssertedName
import java.time.LocalDate

@Stable
data class PersonalInfo(
    val name: String = "",
    val selfAssertedName: SelfAssertedName = SelfAssertedName(),
    val confirmedName: ConfirmedName = ConfirmedName(),
    val nameProvider: String? = null,
    val email: String = "",
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
    )

    companion object {
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
                expiryStamp = System.currentTimeMillis()
            )
        ).toImmutableList()
    }

}