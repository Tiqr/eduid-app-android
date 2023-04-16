package nl.eduid.screens.deleteaccountsecondconfirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import org.tiqr.data.repository.IdentityRepository
import org.tiqr.data.service.DatabaseService
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class DeleteAccountSecondConfirmViewModel @Inject constructor(
    private val repository: PersonalInfoRepository,
    private val db: DatabaseService,
    private val identity: IdentityRepository,
) :
    ViewModel() {
    val uiState = MutableLiveData(UiState())

    fun onInputChange(newValue: String) {
        uiState.value = uiState.value?.copy(fullName = newValue)
    }

    fun onDeleteAccountPressed() = viewModelScope.launch {
        uiState.postValue(uiState.value?.copy(inProgress = true))
        val userDetails = repository.getUserDetails()
        if (userDetails != null) {
            val knownFullName = "${userDetails.givenName} ${userDetails.familyName}"
            val typedFullName = uiState.value?.fullName
            if (knownFullName == typedFullName) {
                val deleteOk = repository.deleteAccount()
                val allIdentities = db.getAllIdentities()

                try {
                    allIdentities.forEach {
                        identity.delete(it)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to cleanup existing identities when deleting account")
                }
                uiState.postValue(
                    uiState.value?.copy(
                        inProgress = false,
                        isDeleted = if (deleteOk) Unit else null,
                        errorData = if (deleteOk) null else ErrorData(
                            "Failed to delete account",
                            "Could not delete account"
                        ),
                    )
                )
            } else {
                uiState.postValue(
                    uiState.value?.copy(
                        inProgress = false,
                        errorData = ErrorData(
                            "Cannot delete account",
                            "Name does not match known name"
                        )
                    )
                )
            }
        }
    }

    fun clearErrorData() {
        uiState.value = uiState.value?.copy(errorData = null)
    }
}