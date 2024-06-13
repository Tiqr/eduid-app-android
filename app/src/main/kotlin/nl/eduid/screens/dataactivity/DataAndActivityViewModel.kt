package nl.eduid.screens.dataactivity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.model.EduIdPerServiceProvider
import nl.eduid.di.model.TokenResponse
import nl.eduid.di.model.UserDetails
import javax.inject.Inject

@HiltViewModel
class DataAndActivityViewModel
    @Inject
    constructor(
        private val assistant: DataAssistant,
    ) : ViewModel() {
        private val locale = Locale.current
        var uiState: UiState by mutableStateOf(UiState())
            private set

        init {
            viewModelScope.launch {
                uiState = uiState.copy(isLoading = true, errorData = null)
                try {
                    val userDetails = assistant.getErringUserDetails()
                    val tokens = assistant.getTokensForUser()
                    uiState = if (userDetails != null) {
                        val uiData = convertToUiData(userDetails, tokens)
                        uiState.copy(isLoading = false, errorData = null, data = uiData)
                    } else {
                        uiState.copy(
                            isLoading = false,
                            errorData = ErrorData(
                                titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                                messageId = R.string.ResponseErrors_ActivityHistoryRetrieveError_COPY,
                            ),
                        )
                    }
                } catch (e: UnauthorizedException) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorData = ErrorData(
                            titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                            messageId = R.string.ResponseErrors_UnauthorizedText_COPY,
                        ),
                    )
                }
            }
        }

        fun clearErrorData() {
            uiState = uiState.copy(errorData = null)
        }

        fun removeService(service: String?) = viewModelScope.launch {
            val serviceId = service ?: return@launch
            uiState = UiState(isLoading = true, errorData = null)
            try {
                val userDetails = assistant.removeService(serviceId)
                val tokens = assistant.getTokensForUser()
                uiState = if (userDetails != null) {
                    val uiData = convertToUiData(userDetails, tokens)
                    UiState(
                        isLoading = false,
                        errorData = null,
                        data = uiData,
                        deleteService = null,
                    )
                } else {
                    UiState(
                        isLoading = false,
                        deleteService = null,
                        errorData = ErrorData(
                            titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                            messageId = R.string.ResponseErrors_ActivityHistoryRetrieveError_COPY,
                        ),
                    )
                }
            } catch (e: UnauthorizedException) {
                uiState = uiState.copy(
                    isLoading = false,
                    deleteService = null,
                    errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.ResponseErrors_UnauthorizedText_COPY,
                    ),
                )
            }
        }

        fun revokeToken() = viewModelScope.launch {
            try {
                if (uiState.revokeToken?.token != null) {
                    val deleteToken = uiState.revokeToken?.token
                    uiState = UiState(isLoading = true, errorData = null)
                    val userDetails = assistant.updateTokens(deleteToken)
                    val tokens = assistant.getTokensForUser()
                    uiState = if (userDetails != null) {
                        val uiData = convertToUiData(userDetails, tokens)
                        UiState(
                            isLoading = false,
                            errorData = null,
                            revokeToken = null,
                            data = uiData,
                        )
                    } else {
                        UiState(
                            isLoading = false,
                            revokeToken = null,
                            errorData = ErrorData(
                                titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                                messageId = R.string.ResponseErrors_ActivityHistoryRetrieveError_COPY,
                            ),
                        )
                    }
                }
            } catch (e: UnauthorizedException) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.ResponseErrors_UnauthorizedText_COPY,
                    ),
                )
            }
        }

        fun showDeleteServiceDialog(provider: ServiceProvider) {
            uiState = uiState.copy(deleteService = provider)
        }

        fun cancelDeleteService() {
            uiState = uiState.copy(deleteService = null)
        }

        fun showDeleteTokenDialog(scopeAccessGrant: ScopeAccessGrant) {
            uiState = uiState.copy(revokeToken = scopeAccessGrant)
        }

        fun cancelRevokeToken() {
            uiState = uiState.copy(revokeToken = null)
        }

        fun handleBackNavigation(goBack: () -> Unit) {
            val isDeletingService = uiState.deleteService != null
            if (isDeletingService) {
                cancelDeleteService()
            } else {
                goBack()
            }
        }

        private fun convertToUiData(userDetails: UserDetails, tokens: List<TokenResponse>? = null): List<ServiceProvider> =
            userDetails.eduIdPerServiceProvider.values.map { service ->
                val scopeAccessGrant = scopeAccessGrant(tokens, service)
                ServiceProvider(
                    providerName = service.serviceName,
                    createdStamp = service.createdAt,
                    firstLoginStamp = service.createdAt,
                    uniqueId = service.value,
                    serviceProviderEntityId = service.serviceProviderEntityId,
                    providerLogoUrl = service.serviceLogoUrl,
                    scopeAccessGrant = scopeAccessGrant,
                )
            }

        private fun scopeAccessGrant(tokens: List<TokenResponse>?, service: EduIdPerServiceProvider): ScopeAccessGrant? {
            val tokensForService: TokenResponse? = tokens?.firstOrNull {
                it.clientId == service.serviceProviderEntityId &&
                    it.type == "ACCESS"
            }
            val isDutch = locale.toLanguageTag().startsWith("nl", true)
            val scopeAccessGrant = tokensForService?.let {
                ScopeAccessGrant(
                    clientId = it.clientId,
                    forProviderName = service.serviceName,
                    token = it,
                    scopeDescription = if (isDutch) {
                        it.scopes
                            ?.firstOrNull()
                            ?.descriptions
                            ?.nl
                    } else {
                        it.scopes
                            ?.firstOrNull()
                            ?.descriptions
                            ?.en
                    },
                    grantedOn = it.createdAt,
                    expireAt = it.expiresIn,
                )
            }
            return scopeAccessGrant
        }
    }