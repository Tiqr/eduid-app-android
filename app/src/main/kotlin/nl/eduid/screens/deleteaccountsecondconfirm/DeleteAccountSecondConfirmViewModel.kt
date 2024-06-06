package nl.eduid.screens.deleteaccountsecondconfirm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.UnauthorizedException
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import org.tiqr.data.repository.IdentityRepository
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class DeleteAccountSecondConfirmViewModel @Inject constructor(
    private val repository: PersonalInfoRepository,
    private val identity: IdentityRepository,
    private val storage: StorageRepository
) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    fun onInputChange(newValue: String) {
        uiState = uiState.copy(fullName = newValue)
    }

    fun onDeleteAccountPressed() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        try {
            val userDetails = repository.getErringUserDetails()
            if (userDetails != null) {
                val knownFullName = "${userDetails.givenName} ${userDetails.familyName}"
                val typedFullName = uiState.fullName
                if (knownFullName == typedFullName) {
                    val deleteOk = repository.deleteAccount()
                    val tiqrSecretForEduIdAccount =
                        identity.identity(userDetails.id).firstOrNull()
                    storage.clearAll()
                    try {
                        if (tiqrSecretForEduIdAccount != null) {
                            identity.delete(tiqrSecretForEduIdAccount.identity)
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to cleanup existing identities when deleting account")
                    }
                    uiState =
                        uiState.copy(
                            inProgress = false,
                            isDeleted = if (deleteOk) Unit else null,
                            errorData = if (deleteOk) null else ErrorData(
                                titleId = R.string.ResponseErrors_DeleteError_Title_COPY,
                                messageId = R.string.ResponseErrors_DeleteError_Description_COPY
                            ),
                        )
                } else {
                    uiState =
                        uiState.copy(
                            inProgress = false,
                            errorData = ErrorData(
                                titleId = R.string.ResponseErrors_DeleteError_Title_COPY,
                                messageId = R.string.ResponseErrors_DeleteError_NameMismatchDescription_COPY
                            )
                        )
                }
            }
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.ResponseErrors_DeleteError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }

    fun clearErrorData() {
        uiState = uiState.copy(errorData = null)
    }
}