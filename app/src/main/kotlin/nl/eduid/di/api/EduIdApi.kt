package nl.eduid.di.api

import nl.eduid.di.model.RequestNewIdRequest
import nl.eduid.di.model.UserDetails
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API endpoints.
 */
interface EduIdApi {
    @POST("myconext/api/idp/magic_link_request/")
    suspend fun requestNewEduId(@Body request: RequestNewIdRequest): Response<String>

    @GET("mobile/api/sp/me")
    suspend fun getUserDetails(): Response<UserDetails>
}