package nl.eduid.screens.personalinfo

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
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
import nl.eduid.di.model.UserDetails
import nl.eduid.di.model.mapToPersonalInfo
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val assistant: DataAssistant,
    private val moshi: Moshi,
) : ViewModel() {
    private val _errorData: MutableStateFlow<ErrorData?> = MutableStateFlow(null)
    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _linkUrl: MutableStateFlow<Intent?> = MutableStateFlow(null)
    val uiState = assistant.observableDetails.map { it ->
        when (it) {
            is SaveableResult.Success -> {
                val personalInfo = mapUserDetailsToPersonalInfo(it.data)
                if (it.saveError != null) {
                    _errorData.emit(it.saveError.toErrorData())
                }
                UiState(
                    isLoading = false,
                    personalInfo = personalInfo,
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
    val hasLinkedInstitution = uiState.map { it.personalInfo.institutionAccounts.isNotEmpty() }

    private suspend fun mapUserDetailsToPersonalInfo(userDetails: UserDetails): PersonalInfo {
        var personalInfo = userDetails.mapToPersonalInfo(moshi)
        val nameMap = mutableMapOf<String, String>()
        for (account in userDetails.linkedAccounts) {
            val mappedName = assistant.getInstitutionName(account.schacHomeOrganization)
            mappedName?.let {
                //If name found, add to list of mapped names
                nameMap[account.schacHomeOrganization] = mappedName
                //Get name provider from FIRST linked account
                if (account.schacHomeOrganization == userDetails.linkedAccounts.firstOrNull()?.schacHomeOrganization) {
                    personalInfo = personalInfo.copy(
                        nameProvider = nameMap[account.schacHomeOrganization]
                            ?: personalInfo.nameProvider
                    )
                }
                //Update UI data to include mapped institution names
                personalInfo =
                    personalInfo.copy(institutionAccounts = personalInfo.institutionAccounts.map { institution ->
                        institution.copy(
                            roleProvider = nameMap[institution.roleProvider]
                                ?: institution.roleProvider
                        )
                    })
            }
        }
        return personalInfo
    }

    fun clearErrorData() = _errorData.update { null }

    fun removeConnection(institutionId: String) = viewModelScope.launch {
        _isProcessing.update { true }
        assistant.removeConnection(institutionId)
        _isProcessing.update { false }
    }

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
