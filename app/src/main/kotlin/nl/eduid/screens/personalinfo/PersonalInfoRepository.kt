package nl.eduid.screens.personalinfo

import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.DeleteServiceRequest
import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.Token
import nl.eduid.di.model.TokenResponse
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

    suspend fun removeService(serviceId: String): UserDetails? = try {
        val tokens = getTokensForUser()
        val tokensForService = tokens?.filter { token ->
            token.clientId == serviceId && token.scopes?.any { scope ->
                scope.name != "openid" && scope.hasValidDescription()
            } ?: false
        }
        val tokensRequest = tokensForService?.map { serviceToken ->
            Token(serviceToken.id, serviceToken.type)
        } ?: emptyList()

        val response = eduIdApi.removeService(
            DeleteServiceRequest(
                serviceProviderEntityId = serviceId, tokens = tokensRequest
            )
        )
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
        Timber.e(e, "Failed to remove service with id $serviceId")
        null
    }

    private suspend fun getTokensForUser(): List<TokenResponse>? = try {
        val tokenResponse = eduIdApi.getTokens()
        if (tokenResponse.isSuccessful) {
            tokenResponse.body()
        } else {
            Timber.w(
                "Failed to remove connection for [${tokenResponse.code()}/${tokenResponse.message()}]${
                    tokenResponse.errorBody()?.string()
                }"
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to get tokens granted for current user")
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

    suspend fun updateName(selfAssertedName: SelfAssertedName): UserDetails? = try {
        val response = eduIdApi.updateName(selfAssertedName)
        if (response.isSuccessful) {
            response.body()
        } else {
            Timber.w(
                "Failed to update name [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }"
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed update name")
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

    suspend fun getStartLinkAccount(): String? = try {
        val response = eduIdApi.getStartLinkAccount()
        if (response.isSuccessful) {
            response.body()?.url
        } else {
            Timber.w(
                "Failed to retrieve start link account URL: [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }"
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to retrieve start link account URL")
        null
    }
}