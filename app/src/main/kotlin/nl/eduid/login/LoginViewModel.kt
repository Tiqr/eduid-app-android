package nl.eduid.login

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
    }

}