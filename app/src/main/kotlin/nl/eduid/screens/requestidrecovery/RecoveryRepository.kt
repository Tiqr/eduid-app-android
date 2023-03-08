package nl.eduid.screens.requestidrecovery

import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.ConfirmPhoneCode
import nl.eduid.di.model.RequestPhoneCode
import timber.log.Timber

class RecoveryRepository(private val api: EduIdApi) {

    suspend fun requestPhoneCode(phoneNumber: String) = try {
        val response = api.requestPhoneCode(RequestPhoneCode(phoneNumber))
        response.isSuccessful
    } catch (e: Exception) {
        Timber.e(e, "Failed to request phone code")
        false
    }

    suspend fun confirmPhoneCode(phoneCode: String) = try {
        val response = api.confirmPhoneCode(ConfirmPhoneCode(phoneCode))
        response.isSuccessful
    } catch (e: Exception) {
        Timber.e(e, "Failed to verify phone code")
        false
    }

}