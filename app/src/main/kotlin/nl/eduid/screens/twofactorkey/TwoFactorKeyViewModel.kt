package nl.eduid.screens.twofactorkey

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
import org.tiqr.data.repository.IdentityRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TwoFactorKeyViewModel @Inject constructor(
    private val identityRepository: IdentityRepository,
) : ViewModel() {

    var uiState by mutableStateOf(TwoFactorData(isLoading = true))
        private set

    init {
        viewModelScope.launch {
            uiState = TwoFactorData(isLoading = false, keys = identityData())
        }
    }

    private suspend fun identityData(): List<IdentityData> {
        val existingList = uiState.keys
        val keyList = identityRepository.allIdentities().firstOrNull()?.let {
            it.map { key ->
                val existingKey =
                    existingList.firstOrNull { it.uniqueKey == key.identity.identifier }
                val isCurrentlyExpanded = existingKey?.isExpanded == true
                IdentityData(
                    uniqueKey = key.identity.identifier,
                    title = key.identity.displayName,
                    subtitle = key.identityProvider.displayName,
                    account = key.identity.displayName,
                    biometricFlag = key.identity.biometricInUse,
                    isExpanded = isCurrentlyExpanded
                )
            }
        } ?: emptyList()
        return keyList
    }

    fun changeBiometric(key: IdentityData, biometricFlag: Boolean) = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true)
        try {
            val identity = identityRepository.identity(key.uniqueKey).firstOrNull()
            if (identity != null) {
                identityRepository.useBiometric(identity.identity, biometricFlag)
                uiState = uiState.copy(isLoading = false, keys = identityData())
            } else {
                uiState = uiState.copy(
                    isLoading = false, errorData = ErrorData(
                        titleId = R.string.err_title_generic_fail,
                        messageId = R.string.err_msg_change_key_lost,
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to change biometric flag for key")
            uiState = uiState.copy(
                isLoading = false, errorData = ErrorData(
                    titleId = R.string.err_title_generic_fail,
                    messageId = R.string.err_msg_generic_unexpected_with_arg,
                    messageArg = e.message ?: e.javaClass.simpleName
                )
            )
        }
    }

    fun onExpand(key: IdentityData?) {
        if (key == null) {
            return
        }
        val newKeyList =
            uiState.keys.map { if (it == key) key.copy(isExpanded = !key.isExpanded) else it }
        uiState = uiState.copy(keys = newKeyList)
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }
}