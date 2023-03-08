package nl.eduid.screens.biometric

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class SignInWithBiometricsContract : ActivityResultContract<Unit, BiometricSignIn>() {
    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(context, SignInWithBiometricsActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): BiometricSignIn =
        when (resultCode) {
            BIOMETRIC_OK -> BiometricSignIn.Success
            BIOMETRIC_FAILED -> {
                if (intent != null) {
                    BiometricSignIn.Failed(
                        intent.getIntExtra(
                            BIOMETRIC_ERRORCODE_KEY,
                            BIOMETRIC_FAILED
                        ), intent.getStringExtra(BIOMETRIC_ERRORMESSAGE_KEY).orEmpty()
                    )
                } else {
                    BiometricSignIn.Failed(BIOMETRIC_FAILED, "")

                }
            }
            BIOMETRIC_CANCELED -> {
                BiometricSignIn.Failed(BIOMETRIC_CANCELED, "")
            }
            else -> BiometricSignIn.Failed(BIOMETRIC_FAILED, "")
        }

    companion object {
        const val BIOMETRIC_FAILED = 400
        const val BIOMETRIC_OK = 200
        const val BIOMETRIC_CANCELED = 0
        const val BIOMETRIC_ERRORCODE_KEY = "errorCodeKey"
        const val BIOMETRIC_ERRORMESSAGE_KEY = "errorMessageKey"
    }
}

sealed class BiometricSignIn {
    object Success : BiometricSignIn()
    data class Failed(val code: Int, val message: String) : BiometricSignIn()
}