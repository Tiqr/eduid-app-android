package nl.eduid.screens.personalinfo

data class PersonalInfo(
    val name: String = "",
    val nameProvider: String = "",
    val nameStatus: InfoStatus = InfoStatus.Final,
    val email: String = "",
    val emailProvider: String = "",
    val emailStatus: InfoStatus = InfoStatus.Final,
    val role: String = "",
    val roleProvider: String = "",
    val roleStatus: InfoStatus = InfoStatus.Final,
    val institution: String = "",
    val institutionProvider: String = "",
    val institutionStatus: InfoStatus = InfoStatus.Final,
) {
    companion object {
        fun demoData(): PersonalInfo {
            return PersonalInfo(
                name = "R. van Hamersdonksveer",
                nameProvider = "Universiteit van Amsterdam",
                nameStatus = InfoStatus.Final,
                email = "r.v.hamersdonksveer@uva.nl",
                emailProvider = "You",
                emailStatus = InfoStatus.Editable,
                role = "Student",
                roleProvider = "",
                roleStatus = InfoStatus.Empty,
                institution = "Universiteit van Amsterdam",
                institutionProvider = "Universiteit van Amsterdam",
                institutionStatus = InfoStatus.Final,
            )
        }
    }

    sealed class InfoStatus {
        object Empty : InfoStatus()
        object Editable : InfoStatus()
        object Final : InfoStatus()
    }

}