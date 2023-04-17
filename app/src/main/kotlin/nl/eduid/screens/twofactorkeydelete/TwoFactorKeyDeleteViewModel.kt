package nl.eduid.screens.twofactorkeydelete

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TwoFactorKeyDeleteViewModel @Inject constructor(): ViewModel()  {
    fun deleteKey(keyId: String, onDeleteConfirmed: () -> Unit) {
        //Delete key logic here

        //go back on complete
        onDeleteConfirmed.invoke()
    }
}