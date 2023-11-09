package nl.eduid

import nl.eduid.screens.personalinfo.PersonalInfoRepository

class CheckRecovery(
    private val personal: PersonalInfoRepository,
) {

    /**
     * Returns true if the authentication available in the app matches the stored key id. This is
     * required in order to keep backwards compatibility. If the account was created outside the app,
     * then the recovery flow is automatically fired by the web page once the app has enrolled.
     * If the app also fires off a recovery flow, this results in a race condition for which of the
     * 2 recoveries (web or app) should be completed.
     * */
    @SuppressWarnings("unused")
    suspend fun shouldAppDoRecoveryForIdentity(keyIdentifier: String): Boolean {
        val userDetails = personal.getUserDetails()
        val isAuthenticated = userDetails != null
        val haveKeyForAuthenticatedAccount = keyIdentifier == userDetails?.id
        return isAuthenticated && haveKeyForAuthenticatedAccount
    }

}