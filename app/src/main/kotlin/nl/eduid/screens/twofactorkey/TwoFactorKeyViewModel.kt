package nl.eduid.screens.twofactorkey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.tiqr.data.model.IdentityWithProvider
import org.tiqr.data.repository.IdentityRepository
import javax.inject.Inject

@HiltViewModel
class TwoFactorKeyViewModel @Inject constructor(
    identityRepository: IdentityRepository,
): ViewModel()  {

    val identities = identityRepository.allIdentities().asLiveData(viewModelScope.coroutineContext).map {
        it.map { key -> convertToUiData(key) }
    }

    private fun convertToUiData(twoFaDetails: IdentityWithProvider): TwoFactorData {
        return TwoFactorData(
            uniqueKey = twoFaDetails.identity.identifier,
            title = twoFaDetails.identity.displayName,
            subtitle = twoFaDetails.identityProvider.displayName,
            account = twoFaDetails.identity.displayName,
            biometricFlag = twoFaDetails.identity.biometricInUse,
        )
    }
}