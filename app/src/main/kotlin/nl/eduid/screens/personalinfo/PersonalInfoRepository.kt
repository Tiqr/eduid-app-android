package nl.eduid.screens.personalinfo

import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.UserDetails
import timber.log.Timber

class PersonalInfoRepository(private val eduIdApi: EduIdApi) {

    suspend fun getUserDetails(): UserDetails? = try {
        val response = eduIdApi.getUserDetails()
        if (response.isSuccessful) {
            response.body()
        } else {
            Timber.w(
                "User details not available [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }"
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to retrieve user details")
        null
    }
}