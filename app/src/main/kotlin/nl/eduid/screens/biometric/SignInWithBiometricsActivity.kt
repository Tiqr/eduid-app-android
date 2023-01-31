package nl.eduid.screens.biometric

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import nl.eduid.R
import nl.eduid.screens.biometric.SignInWithBiometricsContract.Companion.BIOMETRIC_CANCELED
import nl.eduid.screens.biometric.SignInWithBiometricsContract.Companion.BIOMETRIC_ERRORCODE_KEY
import nl.eduid.screens.biometric.SignInWithBiometricsContract.Companion.BIOMETRIC_ERRORMESSAGE_KEY
import nl.eduid.screens.biometric.SignInWithBiometricsContract.Companion.BIOMETRIC_FAILED
import nl.eduid.screens.biometric.SignInWithBiometricsContract.Companion.BIOMETRIC_OK

@AndroidEntryPoint
class SignInWithBiometricsActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errCode, errString)
            setResult(BIOMETRIC_FAILED, Intent().apply {
                putExtra(BIOMETRIC_ERRORCODE_KEY, errCode)
                putExtra(BIOMETRIC_ERRORMESSAGE_KEY, errString)
            })
            biometricPrompt.cancelAuthentication()
            finish()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            setResult(BIOMETRIC_FAILED, Intent().apply {
                putExtra(BIOMETRIC_ERRORCODE_KEY, BIOMETRIC_FAILED)
                putExtra(BIOMETRIC_ERRORMESSAGE_KEY, getString(R.string.biometric_failed_unknown))
            })
            biometricPrompt.cancelAuthentication()
            finish()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            setResult(BIOMETRIC_OK)
            biometricPrompt.cancelAuthentication()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, authenticationCallback)
        val promptInfo = createPromptInfo()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(getString(R.string.auth_biometric_title))
            setConfirmationRequired(false)
            setNegativeButtonText(getString(R.string.auth_biometric_dialog_cancel))
        }.build()

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(BIOMETRIC_CANCELED)
    }
}