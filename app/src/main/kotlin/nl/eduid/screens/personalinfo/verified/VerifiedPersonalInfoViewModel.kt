package nl.eduid.screens.personalinfo.verified

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
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
import nl.eduid.di.model.mapToInstitutionAccount
import javax.inject.Inject

@HiltViewModel
class VerifiedPersonalInfoViewModel @Inject constructor(
    private val assistant: DataAssistant,
) : ViewModel() {

    private val _errorData: MutableStateFlow<ErrorData?> = MutableStateFlow(null)
    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState = assistant.observableDetails.map { details ->
        when (details) {
            is SaveableResult.Success -> {
                if (details.saveError != null) {
                    _errorData.emit(details.saveError.toErrorData())
                }
                UiState(
                    isLoading = false,
                    accounts = (details.data.linkedAccounts.mapNotNull { it.mapToInstitutionAccount() } +
                            details.data.externalLinkedAccounts.mapNotNull { it.mapToInstitutionAccount() }).toImmutableList()
                )
            }

            is SaveableResult.LoadError -> {
                _errorData.emit(details.exception.toErrorData())
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

    fun removeConnection(subjectId: String) {
        viewModelScope.launch {
            _isProcessing.update { true }
            assistant.removeConnection(subjectId)
            _isProcessing.update { false }
        }
    }
}