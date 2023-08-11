package nl.eduid.screens.accountlinked

interface ResultAccountLinked {
    object OK : ResultAccountLinked
    object FailedAlreadyLinkedResult : ResultAccountLinked
    object FailedExpired : ResultAccountLinked

    companion object {
        fun fromRedirectUrl(path: String) = when {
            path.contains("expired", true) -> FailedExpired
            path.contains("already-linked") -> FailedAlreadyLinkedResult
            else -> {
                OK
            }
        }
    }
}