package nl.eduid.screens.editname

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.eduid.ErrorData
import nl.eduid.R
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.UnauthorizedException
import nl.eduid.graphs.EditName
import javax.inject.Inject

@HiltViewModel
class EditNameFormViewModel @Inject constructor(
    private val eduIdApi: EduIdApi,
    private val assistant: DataAssistant,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    init {
        val givenName = savedStateHandle.get<String>(EditName.Form.givenName) ?: ""
        val familyName = savedStateHandle.get<String>(EditName.Form.familyName) ?: ""
        uiState = uiState.copy(givenName = givenName, familyName = familyName)
    }

    fun onGivenNameChange(newValue: String) {
        uiState = uiState.copy(givenName = newValue)
    }

    fun onFamilyNameChange(newValue: String) {
        uiState = uiState.copy(familyName = newValue)
    }

    fun dismissError() {
        uiState = uiState.copy(errorData = null)
    }

    fun updateName() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true, isCompleted = null)
        try {
            val validatedSelfName = SelfAssertedName(
                familyName = uiState.familyName, givenName = uiState.givenName
            )
            val newDetails = assistant.updateName(validatedSelfName)
            uiState = newDetails?.let { _ ->
                uiState.copy(inProgress = false, isCompleted = Unit)
            } ?: uiState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.err_title_generic_fail,
                    messageId = R.string.err_msg_request_fail
                )
            )
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.err_title_generic_fail,
                    messageId = R.string.err_msg_unauthorized_request_fail
                )
            )
        }
    }
}

@Stable
data class UiState(
    val givenName: String = "",
    val familyName: String = "",
    val inProgress: Boolean = false,
    val errorData: ErrorData? = null,
    val isCompleted: Unit? = null,
)