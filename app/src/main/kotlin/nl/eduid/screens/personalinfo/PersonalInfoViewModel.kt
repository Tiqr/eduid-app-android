package nl.eduid.screens.personalinfo

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
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
import nl.eduid.di.model.LinkedAccountUpdateRequest
import nl.eduid.di.model.UserDetails
import nl.eduid.di.model.mapToPersonalInfo
import nl.eduid.flags.FeatureFlag
import nl.eduid.flags.RuntimeBehavior
import nl.eduid.graphs.AccountLinked
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assistant: DataAssistant,
    private val runtimeBehavior: RuntimeBehavior
) : ViewModel() {
    private val _errorData: MutableStateFlow<ErrorData?> = MutableStateFlow(null)
    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _linkUrl: MutableStateFlow<Intent?> = MutableStateFlow(null)

    val uiState = assistant.observableDetails.map {
        when (it) {
            is SaveableResult.Success -> {
                val personalInfo = PersonalInfo.fromUserDetails(it.data, assistant)
                if (it.saveError != null) {
                    _errorData.emit(it.saveError.toErrorData())
                }
                var verifiedFirstNameAccount: PersonalInfo.InstitutionAccount? = null
                var verifiedLastNameAccount: PersonalInfo.InstitutionAccount? = null
                var verifiedDateOfBirthAccount: PersonalInfo.InstitutionAccount? = null
                // Search in linked internal accounts and then external account
                for (linkedAccount in personalInfo.linkedInternalAccounts + personalInfo.linkedExternalAccounts) {
                    if (verifiedFirstNameAccount == null && linkedAccount.givenName != null && linkedAccount.givenName == personalInfo.selfAssertedName.givenName) {
                        verifiedFirstNameAccount = linkedAccount
                    }
                    if (verifiedLastNameAccount == null && linkedAccount.familyName != null && linkedAccount.familyName == personalInfo.selfAssertedName.familyName) {
                        verifiedLastNameAccount = linkedAccount
                    }
                    if (verifiedDateOfBirthAccount == null && linkedAccount.dateOfBirth != null && linkedAccount.dateOfBirth == personalInfo.dateOfBirth) {
                        verifiedDateOfBirthAccount = linkedAccount
                    }
                }
                // It is possible that there's a verified name / birth date, but it doesn't match the one in the profile. In this case we still need to show it
                // So we go through the accounts once more, but do not check for matches anymore
                if (verifiedFirstNameAccount == null || verifiedLastNameAccount != null) {
                    for (linkedAccount in (personalInfo.linkedInternalAccounts + personalInfo.linkedExternalAccounts)) {
                        if (verifiedFirstNameAccount == null && linkedAccount.givenName != null) {
                            verifiedFirstNameAccount = linkedAccount
                        }
                        if (verifiedLastNameAccount == null && linkedAccount.familyName != null) {
                            verifiedLastNameAccount = linkedAccount
                        }
                        if (verifiedDateOfBirthAccount == null && linkedAccount.dateOfBirth != null) {
                            verifiedDateOfBirthAccount = linkedAccount
                        }
                    }
                }

                UiState(
                    isLoading = false,
                    personalInfo = personalInfo,
                    verifiedFirstNameAccount = verifiedFirstNameAccount,
                    verifiedLastNameAccount = verifiedLastNameAccount,
                    verifiedDateOfBirthAccount = verifiedDateOfBirthAccount
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
    val linkUrl = _linkUrl.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3_000),
        initialValue = null,
    )
    val hasLinkedInstitution = uiState.map {
        it.personalInfo.linkedInternalAccounts.isNotEmpty() ||
                it.personalInfo.linkedExternalAccounts.isNotEmpty()
    }

    val identityVerificationEnabled = runtimeBehavior.isFeatureEnabled(FeatureFlag.ENABLE_IDENTITY_VERIFICATION)

    fun clearErrorData() = _errorData.update { null }

    fun requestLinkUrl() = viewModelScope.launch {
        _isProcessing.update { true }
        val url = assistant.getStartLinkAccount()
        if (url != null) {
            _linkUrl.update { createLaunchIntent(url) }
        } else {
            _errorData.update {
                ErrorData(
                    titleId = R.string.ResponseErrors_UnauthorizedTitle_COPY,
                    messageId = R.string.Profile_AccountLinkError_Title_COPY
                )
            }
        }
        _isProcessing.update { false }
    }

    private fun createLaunchIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }
}
