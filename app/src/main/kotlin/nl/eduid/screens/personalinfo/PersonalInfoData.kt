package nl.eduid.screens.personalinfo

import nl.eduid.di.model.ConfirmedName
import nl.eduid.di.model.SelfAssertedName

data class PersonalInfo(
    val name: String = "",
    val seflAssertedName: SelfAssertedName = SelfAssertedName(),
    val confirmedName: ConfirmedName = ConfirmedName(),
    val nameProvider: String? = null,
    val nameStatus: InfoStatus = InfoStatus.Final,
    val email: String = "",
    val emailStatus: InfoStatus = InfoStatus.Final,
    val institutionAccounts: List<InstitutionAccount> = emptyList(),
    val dateCreated: Long = 0,
) {
    val isVerified = institutionAccounts.isNotEmpty()

    data class InstitutionAccount(
        val id: String,
        val role: String,
        val roleProvider: String,
        val institution: String,
        val affiliationString: String,
        val createdStamp: Long,
        val expiryStamp: Long,
    )

    companion object {
        fun demoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
                seflAssertedName = SelfAssertedName("Pratchett", "Terence David John", "Terry"),
                confirmedName = ConfirmedName("Pratchett", "Terence David John"),
                nameProvider = "Universiteit van Amsterdam",
                nameStatus = InfoStatus.Final,
                email = "r.v.hamersdonksveer@uva.nl",
                emailStatus = InfoStatus.Editable,
                institutionAccounts = emptyList(),
            )
        }
    }

    sealed class InfoStatus {
        object Editable : InfoStatus()
        object Final : InfoStatus()
    }

}