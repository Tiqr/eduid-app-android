package nl.eduid.di.api

import nl.eduid.di.model.ConfirmDeactivationCode
import nl.eduid.di.model.ConfirmPhoneCode
import nl.eduid.di.model.DeleteServiceRequest
import nl.eduid.di.model.EmailChangeRequest
import nl.eduid.di.model.EnrollResponse
import nl.eduid.di.model.InstitutionNameResponse
import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.RequestEduIdAccount
import nl.eduid.di.model.RequestPhoneCode
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.TokenResponse
import nl.eduid.di.model.UpdatePasswordRequest
import nl.eduid.di.model.UrlResponse
import nl.eduid.di.model.UserDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Retrofit API endpoints.
 */
interface EduIdApi {
    @POST("/mobile/api/idp/create")
    suspend fun createNewEduIdAccount(
        @Body request: RequestEduIdAccount,
    ): Response<Unit>

    @DELETE("/mobile/api/sp/delete")
    suspend fun deleteAccount(): Response<Unit>

    @POST("/mobile/tiqr/sp/send-phone-code")
    suspend fun requestPhoneCode(
        @Body request: RequestPhoneCode,
    ): Response<Unit>

    @POST("/mobile/tiqr/sp/deactivate-app")
    suspend fun deactivateApp(
        @Body request: ConfirmDeactivationCode,
    ): Response<Unit>

    @POST("/mobile/tiqr/sp/verify-phone-code")
    suspend fun confirmPhoneCode(
        @Body request: ConfirmPhoneCode,
    ): Response<Unit>

    @POST("/mobile/tiqr/sp/re-send-phone-code")
    suspend fun retryRequestPhoneCode(
        @Body request: RequestEduIdAccount,
    ): Response<Unit>

    @GET("mobile/api/sp/me")
    suspend fun getUserDetails(): Response<UserDetails>

    @GET("/mobile/api/sp/oidc/link")
    suspend fun getStartLinkAccount(): Response<UrlResponse>

    @GET("/mobile/tiqr/sp/start-enrollment")
    suspend fun startEnrollment(): Response<EnrollResponse>

    @GET("/mobile/tiqr/sp/send-deactivation-phone-code")
    suspend fun requestDeactivationForKnownPhone(): Response<Unit>

    @GET("/mobile/api/sp/tokens")
    suspend fun getTokens(): Response<List<TokenResponse>>

    @PUT("/mobile/api/sp/tokens")
    suspend fun putTokens(
        @Body tokens: List<TokenResponse>,
    ): Response<UserDetails>

    @GET("/mobile/api/sp/institution/names")
    suspend fun getInstitutionName(
        @Query("schac_home") schac_home: String,
    ): Response<InstitutionNameResponse>

    @GET("/mobile/api/sp/confirm-email")
    suspend fun confirmEmail(
        @Query("h") hash: String,
    ): Response<UserDetails>

    @PUT("/mobile/api/sp/email")
    suspend fun requestEmailChange(
        @Body email: EmailChangeRequest,
    ): Response<UserDetails>

    @PUT("/mobile/api/sp/institution")
    suspend fun removeConnection(
        @Body account: LinkedAccount,
    ): Response<UserDetails>

    @PUT("/mobile/api/sp/service")
    suspend fun removeService(
        @Body serviceId: DeleteServiceRequest,
    ): Response<UserDetails>

    @GET("/mobile/api/sp/personal")
    suspend fun getPersonalData(): Response<String>

    @PUT("/mobile/api/sp/update")
    suspend fun updateName(
        @Body selfName: SelfAssertedName,
    ): Response<UserDetails>

    @PUT("/mobile/api/sp/reset-password-link")
    suspend fun resetPasswordLink(): Response<UserDetails>

    @PUT("/mobile/api/sp/update-password")
    suspend fun updatePassword(
        @Body updatePasswordRequest: UpdatePasswordRequest,
    ): Response<UserDetails>

    @GET("/mobile/api/sp/password-reset-hash-valid")
    suspend fun checkHashIsValid(
        @Query("hash") hash: String,
    ): Response<Boolean>
}