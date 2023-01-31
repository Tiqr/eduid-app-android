package nl.eduid.di.api

import nl.eduid.di.model.RequestNewIdRequest
import org.tiqr.data.api.TokenApi
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API endpoints.
 */
interface EduIdApi {
    @POST("myconext/api/idp/magic_link_request/")
    suspend fun requestNewEduId(@Body request: RequestNewIdRequest): Response<String>
}