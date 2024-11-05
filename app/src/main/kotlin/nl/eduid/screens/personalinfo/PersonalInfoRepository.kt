package nl.eduid.screens.personalinfo

import android.annotation.SuppressLint
import android.os.Environment
import android.util.AtomicFile
import androidx.core.util.writeText
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.assist.processResponse
import nl.eduid.di.model.ConfirmDeactivationCode
import nl.eduid.di.model.ConfirmPhoneCode
import nl.eduid.di.model.DeleteServiceRequest
import nl.eduid.di.model.DeleteTokensRequest
import nl.eduid.di.model.EmailChangeRequest
import nl.eduid.di.model.EnrollResponse
import nl.eduid.di.model.IdpScoping
import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.RequestPhoneCode
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.Token
import nl.eduid.di.model.TokenResponse
import nl.eduid.di.model.UrlResponse
import nl.eduid.di.model.UserDetails
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

class PersonalInfoRepository(
    private val eduIdApi: EduIdApi,
) {
    suspend fun getUserDetailsResult(): Result<UserDetails> {
        val response = eduIdApi.getUserDetails()
        return processResponse(response = response)
    }

    suspend fun removeConnectionResult(linkedAccount: LinkedAccount): Result<UserDetails> {
        val response = eduIdApi.removeConnection(linkedAccount)
        return processResponse(response = response)
    }

    suspend fun getStartLinkAccountResult(): Result<UrlResponse?> {
        val response = eduIdApi.getStartLinkAccount()
        return processResponse(response = response)
    }

    suspend fun getExternalAccountLinkResult(idpScoping: IdpScoping, bankId: String?): Result<UrlResponse?> {
        val response = eduIdApi.getStartExternalAccountLink(idpScoping, bankId)
        return processResponse(response = response)
    }

    suspend fun getUserDetails(): UserDetails? = try {
        val response = eduIdApi.getUserDetails()
        if (response.isSuccessful) {
            response.body()
        } else {
            Timber.w(
                "User details not available [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }",
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to retrieve user details")
        null
    }

    suspend fun getErringUserDetails(): UserDetails? = try {
        val response = eduIdApi.getUserDetails()
        if (response.isSuccessful) {
            response.body()
        } else {
            if (response.code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Unauthorized getUserDetails call")
                throw UnauthorizedException("Unauthorized getUserDetails call")
            } else {
                Timber.w(
                    "User details not available [${response.code()}/${response.message()}]${
                        response.errorBody()?.string()
                    }",
                )
                null
            }
        }
    } catch (e: UnauthorizedException) {
        // propagate unauthorized exception, capture everything else.
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Failed to retrieve user details")
        null
    }

    suspend fun startEnrollment(): EnrollResponse? = try {
        val response = eduIdApi.startEnrollment()
        if (response.isSuccessful) {
            response.body()
        } else {
            Timber.w(
                "Failed to start enrollment [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }",
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to start enrollment")
        null
    }

    suspend fun changeEmail(email: String): Int? = try {
        val response = eduIdApi.requestEmailChange(EmailChangeRequest(email))
        if (response.isSuccessful) {
            response.code()
        } else {
            if (response.code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Unauthorized removeService call")
                throw UnauthorizedException("Unauthorized removeService call")
            } else {
                Timber.w(
                    "Failed to change email to $email: [${response.code()}/${response.message()}]${
                        response.errorBody()?.string()
                    }",
                )
                response.code()
            }
        }
    } catch (e: UnauthorizedException) {
        // propagate unauthorized exception, capture everything else.
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Failed to change email")
        null
    }

    suspend fun confirmEmailUpdate(hash: String): UserDetails? = try {
        val response = eduIdApi.confirmEmail(hash)
        if (response.isSuccessful) {
            response.body()
        } else {
            if (response.code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Unauthorized removeConnection call")
                throw UnauthorizedException("Unauthorized removeConnection call")
            } else {
                Timber.w(
                    "Failed to confirm email [${response.code()}/${response.message()}]${
                        response.errorBody()?.string()
                    }",
                )
                null
            }
        }
    } catch (e: UnauthorizedException) {
        // propagate unauthorized exception, capture everything else.
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Failed to confirm email change")
        null
    }

    suspend fun revokeToken(revokeToken: TokenResponse): UserDetails? = try {
        val deleteRequest = DeleteTokensRequest(listOf(Token(revokeToken.id, revokeToken.type)))

        val response = eduIdApi.removeTokens(deleteRequest)
        if (response.isSuccessful) {
            response.body()
        } else {
            if (response.code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Unauthorized removeService call")
                throw UnauthorizedException("Unauthorized removeService call")
            } else {
                Timber.w(
                    "Failed to remove connection for [${response.code()}/${response.message()}]${
                        response.errorBody()?.string()
                    }",
                )
                null
            }
        }
    } catch (e: UnauthorizedException) {
        // propagate unauthorized exception, capture everything else.
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Failed to revoke token $revokeToken")
        null
    }

    suspend fun removeService(serviceId: String): UserDetails? = try {
        val tokens = getTokensForUser()
        val tokensForService = tokens?.filter { token ->
            token.clientId == serviceId &&
                token.scopes?.any { scope ->
                    scope.name != "openid" && scope.hasValidDescription()
                } ?: false
        }
        val tokensRequest = tokensForService?.map { serviceToken ->
            Token(serviceToken.id, serviceToken.type)
        } ?: emptyList()

        val response = eduIdApi.removeService(
            DeleteServiceRequest(
                serviceProviderEntityId = serviceId,
                tokens = tokensRequest,
            ),
        )
        if (response.isSuccessful) {
            response.body()
        } else {
            if (response.code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Unauthorized removeService call")
                throw UnauthorizedException("Unauthorized removeService call")
            } else {
                Timber.w(
                    "Failed to remove connection for [${response.code()}/${response.message()}]${
                        response.errorBody()?.string()
                    }",
                )
                null
            }
        }
    } catch (e: UnauthorizedException) {
        // propagate unauthorized exception, capture everything else.
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Failed to remove service with id $serviceId")
        null
    }

    suspend fun getTokensForUser(): List<TokenResponse>? = try {
        val tokenResponse = eduIdApi.getTokens()
        if (tokenResponse.isSuccessful) {
            tokenResponse.body()
        } else {
            Timber.w(
                "Failed to remove connection for [${tokenResponse.code()}/${tokenResponse.message()}]${
                    tokenResponse.errorBody()?.string()
                }",
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to get tokens granted for current user")
        null
    }

    suspend fun updateName(selfAssertedName: SelfAssertedName): UserDetails? = try {
        val response = eduIdApi.updateName(selfAssertedName)
        if (response.isSuccessful) {
            response.body()
        } else {
            if (response.code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Unauthorized updateName call")
                throw UnauthorizedException("Unauthorized updateName call")
            } else {
                Timber.w(
                    "Failed to update name [${response.code()}/${response.message()}]${
                        response.errorBody()?.string()
                    }",
                )
                null
            }
        }
    } catch (e: UnauthorizedException) {
        // propagate unauthorized exception, capture everything else.
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Failed update name")
        null
    }

    suspend fun getInstitutionName(schacHome: String): String? = try {
        val response = eduIdApi.getInstitutionName(schacHome)
        if (response.isSuccessful) {
            response.body()?.displayNameEn
        } else {
            if (response.code() == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Unauthorized getInstutitionName call")
                throw UnauthorizedException("Unauthorized getInstitutionName call")
            } else {
                Timber.w(
                    "Institution name lookup failed. [${response.code()}/${response.message()}]${
                        response.errorBody()?.string()
                    }",
                )
                null
            }
        }
    } catch (e: UnauthorizedException) {
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Failed to retrieve institution name")
        null
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun downloadPersonalData(): Boolean = try {
        val response = eduIdApi.getPersonalData()
        if (response.isSuccessful) {
            val personalDataJson = response.body()
            if (personalDataJson != null) {
                val downloadFolder =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val now = Calendar.getInstance().time
                val format = SimpleDateFormat("dMy")
                val timestamp = format.format(now)
                val file = AtomicFile(File(downloadFolder, "eduid_export_$timestamp.json"))
                file.writeText(personalDataJson)
                true
            } else {
                false
            }
        } else {
            Timber.w(
                "Failed to get personal data [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }",
            )
            false
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to download and save personal data.")
        false
    }

    suspend fun deleteAccount() = try {
        val response = eduIdApi.deleteAccount()
        response.isSuccessful
    } catch (e: Exception) {
        Timber.e(e, "Failed to delete account")
        false
    }

    suspend fun resetPasswordLink(): UserDetails? = try {
        val response = eduIdApi.resetPasswordLink()
        if (response.isSuccessful) {
            response.body()
        } else {
            Timber.w(
                "Failed to send password link: [${response.code()}/${response.message()}]${
                    response.errorBody()?.string()
                }",
            )
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to send password link")
        null
    }

    suspend fun requestDeactivationForKnownPhone() = try {
        val response = eduIdApi.requestDeactivationForKnownPhone()
        response.isSuccessful
    } catch (e: Exception) {
        Timber.e(e, "Failed to request deactivation phone code")
        false
    }

    suspend fun requestPhoneCode(phoneNumber: String) = try {
        val response = eduIdApi.requestPhoneCode(RequestPhoneCode(phoneNumber))
        response.isSuccessful
    } catch (e: Exception) {
        Timber.e(e, "Failed to request phone code")
        false
    }

    suspend fun confirmPhoneCode(phoneCode: String) = try {
        val response = eduIdApi.confirmPhoneCode(ConfirmPhoneCode(phoneCode))
        response.isSuccessful
    } catch (e: Exception) {
        Timber.e(e, "Failed to verify phone code")
        false
    }

    suspend fun deactivateApp(phoneCode: String) = try {
        val response = eduIdApi.deactivateApp(ConfirmDeactivationCode(phoneCode))
        response.isSuccessful
    } catch (e: Exception) {
        Timber.e(e, "Failed to deactivate app")
        false
    }
}