package nl.eduid.screens.security

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.model.UserDetails
import nl.eduid.graphs.Security
import org.tiqr.data.repository.IdentityRepository
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val assistant: DataAssistant,
    private val identity: IdentityRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var uiState: SecurityScreenData by mutableStateOf(SecurityScreenData())
        private set

    init {
        val confirmEmailHash =
            savedStateHandle.get<String>(Security.ConfirmEmail.confirmEmailHash) ?: ""

        viewModelScope.launch {
            if (confirmEmailHash.isNotEmpty()) {
                confirmEmail(confirmEmailHash)
            } else {
                loadUserData()
            }
        }
    }

    private suspend fun confirmEmail(hash: String) {
        uiState = uiState.copy(isLoading = true, errorData = null)
        try {
            val userDetails = assistant.confirmEmail(hash)
            uiState = fillUserData(userDetails, isChangeEmail = true)
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    private suspend fun loadUserData() {
        uiState = uiState.copy(isLoading = true, errorData = null)
        try {
            val userDetails = assistant.getErringUserDetails()
            uiState = fillUserData(userDetails)
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    private suspend fun fillUserData(userDetails: UserDetails?, isChangeEmail: Boolean = false) =
        if (userDetails != null) {
            val identity = identity.identity(userDetails.id).firstOrNull()
            val provider = identity?.identityProvider?.displayName
            uiState.copy(
                isLoading = false, errorData = null, email = userDetails.email,
                twoFAProvider = provider,
                hasPassword = userDetails.hasPasswordSet(),
            )
        } else {
            if (isChangeEmail) {
                uiState.copy(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.Generic_RequestError_Title_COPY,
                        messageId = R.string.Email_UpdateError_COPY
                    )
                )
            } else {
                uiState.copy(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                        messageId = R.string.ResponseErrors_PersonalDetailsRetrieveError_COPY
                    )
                )
            }
        }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }
}