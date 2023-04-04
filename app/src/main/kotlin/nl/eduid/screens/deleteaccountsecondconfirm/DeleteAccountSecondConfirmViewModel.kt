package nl.eduid.screens.deleteaccountsecondconfirm

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.di.api.EduIdApi
import nl.eduid.graphs.ManageAccountRoute
import javax.inject.Inject


@HiltViewModel
class DeleteAccountSecondConfirmViewModel @Inject constructor(private val eduIdApi: EduIdApi) : ViewModel() {
    val fullNameInput = MutableLiveData("")

    fun onInputChange(newValue: String) {
        fullNameInput.value = newValue
    }

    fun onDeleteAccountPressed() {
        if (fullNameInput.value?.isNotBlank() == true) {

        }
    }
}