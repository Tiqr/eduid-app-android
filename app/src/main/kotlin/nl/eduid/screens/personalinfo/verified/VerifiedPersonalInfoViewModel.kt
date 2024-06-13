package nl.eduid.screens.personalinfo.verified

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.SaveableResult
import nl.eduid.di.assist.toErrorData
import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.UserDetails
import nl.eduid.di.model.mapToInstitutionAccount
import nl.eduid.graphs.VerifiedPersonalInfoRoute
import java.net.URLDecoder
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VerifiedPersonalInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assistant: DataAssistant,
    moshi: Moshi,
) : ViewModel() {

    private val json =
        savedStateHandle.get<String>(VerifiedPersonalInfoRoute.verifiedByAccountArg)
    private val _errorData: MutableStateFlow<ErrorData?> = MutableStateFlow(null)
    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val adapter = moshi.adapter(LinkedAccount::class.java)
    private val asJson = URLDecoder.decode(json, Charsets.UTF_8.name())
    private val linkedAccount = adapter.fromJson(asJson)
    val uiState = flow {
        if (linkedAccount != null) {
            val institutionAccount = linkedAccount.mapToInstitutionAccount(asJson)
            val mappedName = assistant.getInstitutionName(linkedAccount.schacHomeOrganization) ?: ""
            emit(
                UiState(
                    isLoading = false, verifier = institutionAccount?.copy(
                        roleProvider = mappedName
                    )
                )
            )
        } else {
            _errorData.emit(
                ErrorData(
                    titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                    messageId = R.string.ResponseErrors_PersonalDetailsRetrieveError_COPY
                )
            )
            emit(UiState(isLoading = false))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState(isLoading = true)
    )
    val errorData = _errorData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3_000),
        initialValue = null,
    )
    val isProcessing = _isProcessing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3_000),
        initialValue = false,
    )

    fun clearErrorData() = _errorData.update { null }

    fun removeConnection() {
        viewModelScope.launch {
            _isProcessing.update { true }
            assistant.removeConnection(linkedAccount)
            _isProcessing.update { false }
        }
    }

    private suspend fun convertToState(details: SaveableResult<UserDetails>?, id: String) =
        when (details) {
            is SaveableResult.LoadError -> {
                _errorData.emit(details.exception.toErrorData())
                UiState(isLoading = false)
            }

            is SaveableResult.Success -> {
                val account =
                    details.data.linkedAccounts.firstOrNull { it.institutionIdentifier == id }
                if (account != null) {
                    val institutionAccount =
                        account.mapToInstitutionAccount(adapter.toJson(account))
                    val mappedName =
                        assistant.getInstitutionName(account.schacHomeOrganization) ?: ""
                    if (details.saveError != null) {
                        _errorData.emit(details.saveError.toErrorData())
                    }
                    UiState(
                        isLoading = false, verifier = institutionAccount?.copy(
                            roleProvider = mappedName
                        )
                    )
                } else {
                    UiState(isLoading = false, verifier = null)
                }
            }

            null -> {
                _errorData.emit(
                    ErrorData(
                        titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                        messageId = R.string.ResponseErrors_PersonalDetailsRetrieveError_COPY
                    )
                )
                UiState(isLoading = false)
            }
        }
}