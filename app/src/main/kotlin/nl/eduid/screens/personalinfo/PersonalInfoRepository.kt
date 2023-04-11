package nl.eduid.screens.personalinfo

import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.LinkedAccount
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

    suspend fun removeConnection(linkedAccount: LinkedAccount): UserDetails? = try {
        val response = eduIdApi.removeConnection(linkedAccount)
        if (response.isSuccessful) {
            response.body()
        } else {
            Timber.w(
                "Failed to remove connection for [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }"
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to remove connection for ${linkedAccount.institutionIdentifier}")
        null
    }

    suspend fun getInstitutionName(schac_home: String): String? = try {
        val response = eduIdApi.getInstitutionName(schac_home)
        if (response.isSuccessful) {
            response.body()?.displayNameEn
        } else {
            Timber.w(
                "Institution name lookup failed. [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }"
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to retrieve institution name")
        null
    }
}