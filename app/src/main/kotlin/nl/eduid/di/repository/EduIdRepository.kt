package nl.eduid.di.repository

import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.RequestEduIdAccount
import timber.log.Timber

/**
 * Repository to handle enrollment challenges.
 */
class EduIdRepository(
    val api: EduIdApi,
) {
    suspend fun requestEnroll(request: RequestEduIdAccount): Int = try {
        val response = api.createNewEduIdAccount(request)
        response.code()
    } catch (e: Exception) {
        Timber.e(e, "Failed to create new edu id accout")
        418
    }
}