package nl.eduid.screens.manageaccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.di.api.EduIdApi
import nl.eduid.graphs.ManageAccountRoute
import javax.inject.Inject

@HiltViewModel
class ManageAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eduIdApi: EduIdApi,
    ) : ViewModel() {
    init {
        val dateString: String = savedStateHandle.get<String>(ManageAccountRoute.dateArg) ?: ""
    }
}