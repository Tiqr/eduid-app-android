package nl.eduid.di.api

import nl.eduid.di.model.ConfirmPhoneCode
import nl.eduid.di.model.RequestEduIdAccount
import nl.eduid.di.model.RequestPhoneCode
import nl.eduid.di.model.UserDetails
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API endpoints.
 */
interface EduIdApi {

    @POST("/mobile/api/idp/create")
    suspend fun createNewEduIdAccount(@Body request: RequestEduIdAccount): Response<Unit>

    @POST("/mobile/tiqr/sp/send-phone-code")
    suspend fun requestPhoneCode(@Body request: RequestPhoneCode): Response<Unit>

    @POST("/mobile/tiqr/sp/verify-phone-code")
    suspend fun confirmPhoneCode(@Body request: ConfirmPhoneCode): Response<Unit>

    @POST("/mobile/tiqr/sp/re-send-phone-code")
    suspend fun retryRequestPhoneCode(@Body request: RequestEduIdAccount): Response<Unit>

    @GET("mobile/api/sp/me")
    suspend fun getUserDetails(): Response<UserDetails>
}