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
import nl.eduid.di.assist.DataAssistant
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.UnauthorizedException
import nl.eduid.graphs.EditName
import javax.inject.Inject

@HiltViewModel
class EditNameFormViewModel @Inject constructor(
    private val assistant: DataAssistant,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    init {
        val chosenName = savedStateHandle.get<String>(EditName.Form.chosenName) ?: ""
        val familyName = savedStateHandle.get<String>(EditName.Form.familyName) ?: ""
        val canEditFamilyName =
            savedStateHandle.get<Boolean>(EditName.Form.canEditFamilyName) ?: true
        uiState = uiState.copy(
            chosenName = chosenName,
            familyName = familyName,
            canEditFamilyName = canEditFamilyName
        )
    }

    fun onChosenNameChange(newValue: String) {
        uiState = uiState.copy(chosenName = newValue)
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
                familyName = uiState.familyName, chosenName = uiState.chosenName
            )
            val newDetails = assistant.updateName(validatedSelfName)
            uiState = newDetails?.let { _ ->
                uiState.copy(inProgress = false, isCompleted = Unit)
            } ?: uiState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_GeneralRequestError_COPY
                )
            )
        } catch (e: UnauthorizedException) {
            uiState = uiState.copy(
                inProgress = false, errorData = ErrorData(
                    titleId = R.string.Generic_RequestError_Title_COPY,
                    messageId = R.string.ResponseErrors_UnauthorizedText_COPY
                )
            )
        }
    }
}

@Stable
data class UiState(
    val chosenName: String = "",
    val familyName: String = "",
    val inProgress: Boolean = false,
    val canEditFamilyName: Boolean = true,
    val errorData: ErrorData? = null,
    val isCompleted: Unit? = null,
)