package nl.eduid.screens.verifywithid.input

import androidx.lifecycle.ViewModel

data class GenerateCodePayload(
    val firstName: String,
    val lastName: String,
    val birthDateMillis: Long,
)

class VerifyWithIdInputViewModel: ViewModel() {

    fun generateCode(payload: GenerateCodePayload) {
        TODO()
    }

}