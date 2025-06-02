package nl.eduid.di.api

import nl.eduid.di.model.ConfirmDeactivationCode
import nl.eduid.di.model.ConfirmPhoneCode
import nl.eduid.di.model.ControlCode
import nl.eduid.di.model.ControlCodeRequest
import nl.eduid.di.model.CreateWithOneTimeCodeResponse
import nl.eduid.di.model.DeleteServiceRequest
import nl.eduid.di.model.DeleteTokensRequest
import nl.eduid.di.model.EmailChangeRequest
import nl.eduid.di.model.EnrollResponse
import nl.eduid.di.model.IdpScoping
import nl.eduid.di.model.InstitutionNameResponse
import nl.eduid.di.model.LinkedAccountUpdateRequest
import nl.eduid.di.model.RequestEduIdAccount
import nl.eduid.di.model.RequestPhoneCode
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.TokenResponse
import nl.eduid.di.model.UpdatePasswordRequest
import nl.eduid.di.model.UrlResponse
import nl.eduid.di.model.UserDetails
import nl.eduid.di.model.VerifyIssuer
import nl.eduid.di.model.VerifyOneTimeCodeRequest
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

    @POST("/mobile/api/idp/v2/create")
    suspend fun createNewEduIdAccountWithOneTimeCode(
        @Body request: RequestEduIdAccount,
    ): Response<CreateWithOneTimeCodeResponse>

    @GET("/mobile/api/idp/v2/resend_code_request")
    suspend fun resendOneTimeCodeRequest(
        @Query("hash") hash: String,
    ): Response<Unit>

    @PUT("/mobile/api/idp/v2/verify_code_request")
    suspend fun verifyOneTimeCodeRequest(
        @Body request: VerifyOneTimeCodeRequest,
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

    @GET("/mobile/api/sp/institution/names")
    suspend fun getInstitutionName(
        @Query("schac_home") schac_home: String,
    ): Response<InstitutionNameResponse>

    @PUT("/mobile/api/sp/prefer-linked-account")
    suspend fun preferLinkedAccount(
        @Body request: LinkedAccountUpdateRequest,
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
        @Body updateRequest: LinkedAccountUpdateRequest,
    ): Response<UserDetails>

    @PUT("/mobile/api/sp/service")
    suspend fun removeService(
        @Body serviceId: DeleteServiceRequest,
    ): Response<UserDetails>

    @PUT("/mobile/api/sp/tokens")
    suspend fun removeTokens(
        @Body tokens: DeleteTokensRequest,
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

    @GET("/mobile/api/sp/verify/link")
    suspend fun getStartExternalAccountLink(
        @Query("idpScoping") idpScoping: IdpScoping,
        @Query("bankId") bankId: String?
    ): Response<UrlResponse>

    @GET("/mobile/api/sp/idin/issuers")
    suspend fun getVerifyIssuers(): Response<List<VerifyIssuer>>

    @POST("/mobile/api/sp/control-code")
    suspend fun createControlCode(@Body request: ControlCodeRequest): Response<ControlCode>

    @DELETE("/mobile/api/sp/control-code")
    suspend fun deleteControlCode(): Response<UserDetails>
}