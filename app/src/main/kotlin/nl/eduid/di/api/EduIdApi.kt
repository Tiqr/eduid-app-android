package nl.eduid.di.api

import nl.eduid.di.model.RequestEduIdAccount
import nl.eduid.di.model.UserDetails
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API endpoints.
 */
interface EduIdApi {

    @POST("/mobile/api/idp/create")
    suspend fun createNewEduIdAccount(@Body request: RequestEduIdAccount): Response<Unit>

    @GET("mobile/api/sp/me")
    suspend fun getUserDetails(): Response<UserDetails>
}