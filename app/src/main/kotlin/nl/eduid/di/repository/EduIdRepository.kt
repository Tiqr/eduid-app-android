package nl.eduid.di.repository

import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.CreateWithOneTimeCodeResponse
import nl.eduid.di.model.RequestEduIdAccount
import nl.eduid.di.model.VerifyOneTimeCodeRequest
import nl.eduid.flags.FeatureFlag
import nl.eduid.flags.RuntimeBehavior
import timber.log.Timber

/**
 * Repository to handle enrollment challenges.
 */
class EduIdRepository(
    val api: EduIdApi
) {
    data class CreateAccountResult(
        val code: Int,
        val hash: String? = null
    )

    suspend fun requestEnroll(request: RequestEduIdAccount): CreateAccountResult = try {
        val response = api.createNewEduIdAccountWithOneTimeCode(request)
        CreateAccountResult(response.code(), response.body()?.hash)
    } catch (e: Exception) {
        Timber.e(e, "Failed to create new eduID account")
        CreateAccountResult(418)
    }

    suspend fun resendOneTimeCode(hash: String) {
        api.resendOneTimeCodeRequest(hash)
    }

    suspend fun verifyOneTimeCode(hash: String, code: String) = try {
        val response = api.verifyOneTimeCodeRequest(
            VerifyOneTimeCodeRequest(
                hash = hash,
                code = code
            )
        )
        Pair(response.code(), response.body())
    } catch (ex: Exception) {
        Timber.w(ex, "Failed to verify one-time code")
        Pair(400, null)
    }

    suspend fun verifyEmailCode(code: String) = try {
        val response = api.verifyEmailCode(
            VerifyOneTimeCodeRequest(
                hash = null,
                code = code
            )
        )
        Pair(response.code(), response.body())
    } catch (ex: Exception) {
        Timber.w(ex, "Failed to verify one-time code for email change")
        Pair(400, null)
    }

    suspend fun verifyPasswordCode(code: String) = try {
        val response = api.verifyPasswordCode(
            VerifyOneTimeCodeRequest(
                hash = null,
                code = code
            )
        )
        Pair(response.code(), response.body())
    } catch (ex: Exception) {
        Timber.w(ex, "Failed to verify one-time code for password change")
        Pair(400, null)
    }
}