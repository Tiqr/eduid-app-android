package nl.eduid.screens.twofactorkey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import org.tiqr.data.model.IdentityWithProvider
import org.tiqr.data.repository.IdentityRepository
import javax.inject.Inject

@HiltViewModel
class TwoFactorKeyViewModel @Inject constructor(
    identityRepository: IdentityRepository,
) : ViewModel() {

    val uiState = identityRepository.allIdentities()
        .onStart { TwoFactorData(isLoading = true, keys = emptyList()) }
        .asLiveData(viewModelScope.coroutineContext).map {
            val keyList = it.map { key -> convertToUiData(key) }
            TwoFactorData(isLoading = false, keys = keyList)
        }

    private fun convertToUiData(twoFaDetails: IdentityWithProvider): IdentityData {
        return IdentityData(
            uniqueKey = twoFaDetails.identity.identifier,
            title = twoFaDetails.identity.displayName,
            subtitle = twoFaDetails.identityProvider.displayName,
            account = twoFaDetails.identity.displayName,
            biometricFlag = twoFaDetails.identity.biometricInUse,
        )
    }
}