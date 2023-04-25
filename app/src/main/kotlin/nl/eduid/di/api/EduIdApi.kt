package nl.eduid.di.api

import nl.eduid.di.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API endpoints.
 */
interface EduIdApi {

    @POST("/mobile/api/idp/create")
    suspend fun createNewEduIdAccount(@Body request: RequestEduIdAccount): Response<Unit>

    @DELETE("/mobile/api/sp/delete")
    suspend fun deleteAccount(): Response<Unit>

    @POST("/mobile/tiqr/sp/send-phone-code")
    suspend fun requestPhoneCode(@Body request: RequestPhoneCode): Response<Unit>

    @POST("/ /mobile/tiqr/sp/deactivate-app")
    suspend fun deactivateApp(@Body request: ConfirmPhoneCode): Response<Unit>

    @POST("/mobile/tiqr/sp/verify-phone-code")
    suspend fun confirmPhoneCode(@Body request: ConfirmPhoneCode): Response<Unit>

    @POST("/mobile/tiqr/sp/re-send-phone-code")
    suspend fun retryRequestPhoneCode(@Body request: RequestEduIdAccount): Response<Unit>

    @GET("mobile/api/sp/me")
    suspend fun getUserDetails(): Response<UserDetails>

    @GET("/mobile/api/sp/oidc/link")
    suspend fun getStartLinkAccount(): Response<UrlResponse>

    @GET("/mobile/tiqr/sp/start-enrollment")
    suspend fun startEnrollment(): Response<EnrollResponse>

    @GET("//mobile/tiqr/sp/send-deactivation-phone-code")
    suspend fun requestDeactivationForKnownPhone(): Response<Unit>

    @GET("/mobile/api/sp/tokens")
    suspend fun getTokens(): Response<List<TokenResponse>>

    @GET("/mobile/api/sp/institution/names")
    suspend fun getInstitutionName(@Query("schac_home") schac_home: String): Response<InstitutionNameResponse>

    @PUT("/mobile/api/sp/email")
    suspend fun requestEmailChange(@Body email: EmailChangeRequest): Response<UserDetails>

    @PUT("/mobile/api/sp/institution")
    suspend fun removeConnection(@Body account: LinkedAccount): Response<UserDetails>

    @PUT("/mobile/api/sp/service")
    suspend fun removeService(@Body serviceId: DeleteServiceRequest): Response<UserDetails>

    @GET("/mobile/api/sp/personal")
    suspend fun getPersonalData(): Response<String>

    @PUT("/mobile/api/sp/update")
    suspend fun updateName(@Body selfName: SelfAssertedName): Response<UserDetails>

    @PUT("/mobile/api/sp/reset-password-link")
    suspend fun resetPasswordLink(): Response<UserDetails>

    @PUT("/mobile/api/sp/update-password")
    suspend fun updatePassword(@Body updatePasswordRequest: UpdatePasswordRequest): Response<UserDetails>

    @GET("/mobile/api/sp/password-reset-hash-valid")
    suspend fun checkHashIsValid(@Query("hash") hash: String): Response<Boolean>
}