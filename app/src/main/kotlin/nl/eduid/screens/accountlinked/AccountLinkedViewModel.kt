package nl.eduid.screens.accountlinked

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.assist.SaveableResult
import nl.eduid.di.assist.toErrorData
import nl.eduid.graphs.AccountLinked
import nl.eduid.screens.personalinfo.PersonalInfo
import javax.inject.Inject

@HiltViewModel
class AccountLinkedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assistant: DataAssistant
) : ViewModel() {
    private val _accountLinked: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _errorData: MutableStateFlow<ErrorData?> = MutableStateFlow(null)
    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState = assistant.observableDetails.map {
        when (it) {
            is SaveableResult.Success -> {
                val personalInfo = PersonalInfo.fromUserDetails(it.data, assistant)
                if (it.saveError != null) {
                    _errorData.emit(it.saveError.toErrorData())
                }
                UiState(
                    isLoading = false,
                    personalInfo = personalInfo
                )
            }

            is SaveableResult.LoadError -> {
                _errorData.emit(it.exception.toErrorData())
                UiState(isLoading = false)
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3_000),
        initialValue = UiState(isLoading = true),
    )

    val isRegistrationFlow: Boolean = savedStateHandle.get<Boolean>(AccountLinked.isRegistrationFlowArg) ?: false

    val accountLinked = _accountLinked.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3_000),
        initialValue = false,
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

    init {
        viewModelScope.launch {
            refreshPersonalInfo()
        }
    }

    fun clearErrorData() = _errorData.update { null }

    fun findLinkedAccount(personalInfo: PersonalInfo?, institutionId: String?): PersonalInfo.InstitutionAccount? {
        val completeList = (personalInfo?.linkedInternalAccounts ?: listOf()) + (personalInfo?.linkedExternalAccounts ?: listOf())
        completeList.forEach {
            if (it.institution == institutionId || it.subjectId == institutionId) {
                return it
            }
        }
        return completeList.firstOrNull()
    }

    fun isFirstLinkedAccount(personalInfo: PersonalInfo): Boolean {
        return personalInfo.linkedInternalAccounts.size + personalInfo.linkedExternalAccounts.size < 2
    }

    fun preferLinkedAccount(linkedAccount: PersonalInfo.InstitutionAccount) {
        _isProcessing.update { true }
        viewModelScope.launch {
            if (assistant.preferLinkedAccount(linkedAccount.updateRequest)) {
                _accountLinked.update { true }
            } else {
                _errorData.update {
                    ErrorData(
                        titleId = R.string.ExternalAccountLinkingError_Title_COPY,
                        messageId = R.string.ExternalAccountLinkingError_Subtitle_COPY
                    )
                }
            }
            _isProcessing.update { false }
        }
    }

    fun refreshPersonalInfo() {
        viewModelScope.launch {
            assistant.refreshDetails()
        }
    }
}