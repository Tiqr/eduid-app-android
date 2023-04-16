package nl.eduid.screens.personalinfo

import nl.eduid.di.model.SelfAssertedName

data class PersonalInfo(
    val name: String = "",
    val seflAssertedName: SelfAssertedName = SelfAssertedName(),
    val nameProvider: String? = null,
    val nameStatus: InfoStatus = InfoStatus.Final,
    val email: String = "",
    val emailStatus: InfoStatus = InfoStatus.Final,
    val institutionAccounts: List<InstitutionAccount> = emptyList(),
    val dateCreated: Long = 0,
) {
    data class InstitutionAccount(
        val role: String,
        val roleProvider: String,
        val institution: String,
        val affiliationString: String,
        val status: InfoStatus = InfoStatus.Final,
        val createdStamp: Long,
        val expiryStamp: Long,
    )

    companion object {
        fun demoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
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