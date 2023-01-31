package nl.eduid.di.repository

import nl.eduid.di.api.EduIdApi
import nl.eduid.di.model.RequestNewIdRequest
import java.util.UUID

/**
 * Repository to handle enrollment challenges.
 */
class EduIdRepository(
    val api: EduIdApi,
) {
    suspend fun requestEnroll(uuid: UUID) {
        api.requestNewEduId(RequestNewIdRequest(RequestNewIdRequest.User(email = "email44@email.com", givenName = "Tester", familyName = "Testerson"), authenticationRequestId = uuid.toString())).run {

        }
    }
}