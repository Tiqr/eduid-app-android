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
    val api: EduIdApi,
    runtimeBehavior: RuntimeBehavior
) {
    val isLoginWithEmailCodeEnabled = runtimeBehavior.isFeatureEnabled(FeatureFlag.LOG_IN_WITH_EMAIL_CODE)

    data class CreateAccountResult(
        val code: Int,
        val hash: String? = null
    )

    suspend fun requestEnroll(request: RequestEduIdAccount): CreateAccountResult = try {
        val response = if (isLoginWithEmailCodeEnabled) {
            api.createNewEduIdAccountWithOneTimeCode(request)
        } else {
            api.createNewEduIdAccount(request)
        }
        CreateAccountResult(response.code(), (response.body() as? CreateWithOneTimeCodeResponse)?.hash)
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
        response.code()
    } catch (ex: Exception) {
        Timber.w(ex, "Failed to verify one-time code")
        400
    }
}