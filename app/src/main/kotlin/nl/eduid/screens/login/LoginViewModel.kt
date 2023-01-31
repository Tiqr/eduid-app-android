package nl.eduid.screens.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.tiqr.data.model.SecretCredential
import org.tiqr.data.repository.IdentityRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: IdentityRepository) : ViewModel() {
    val loginValid = MutableLiveData<Boolean?>(null)

    fun isLoginValid(credential: SecretCredential) = viewModelScope.launch {
        val identities = repository.getAllIdentities()
        if (identities.isEmpty()) {
            return@launch
        } else {
            try {
                val canDecrypt = repository.canDecryptWithPassword(
                    identities[0], credential.password, credential.type
                )
                loginValid.postValue(true)
                Timber.e("Can decrypt $canDecrypt")
            } catch (e: Exception) {
                loginValid.postValue(false)
                Timber.e("Decryption failed. Invalid password.")
            }

        }
    }

}