package nl.eduid.screens.personalinfo

data class PersonalInfo(
    val name: String = "",
    val nameProvider: String = "",
    val nameStatus: InfoStatus = InfoStatus.Final,
    val email: String = "",
    val emailProvider: String = "",
    val emailStatus: InfoStatus = InfoStatus.Final,
    val institutionAccounts: List<InstitutionAccount> = emptyList(),
    val dateCreated: Long = 0,
) {
    companion object {
        data class InstitutionAccount(
            val id: String,
            val role: String,
            val roleProvider: String,
            val institution: String,
            val affiliationString: String,
            val status: InfoStatus = InfoStatus.Final,
            val createdStamp: Long,
            val expiryStamp: Long,
        )
        fun demoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
                nameProvider = "Universiteit van Amsterdam",
                nameStatus = InfoStatus.Final,
                email = "r.v.hamersdonksveer@uva.nl",
                emailProvider = "You",
                emailStatus = InfoStatus.Editable,
                institutionAccounts = emptyList(),
            )
        }
    }

    sealed class InfoStatus {
        object Empty : InfoStatus()
        object Editable : InfoStatus()
        object Final : InfoStatus()
    }

}