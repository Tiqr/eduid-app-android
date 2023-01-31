package nl.eduid.screens.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import timber.log.Timber

object BiometricksUtil {

    fun getBiometricksStatus(context: Context): Biometricks {
        val biometricManager = BiometricManager.from(context)

        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Biometricks.Available
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            -> {
                Timber.w("Biometric hardware is either unavailable or does not exist. Skipping biometric registration.")
                Biometricks.Unavailable
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Timber.w("No biometrics are enrolled")
                Biometricks.NoneEnrolled
            }
            else -> {
                Biometricks.Unavailable
            }
        }
    }
}

sealed class Biometricks {
    object Unavailable : Biometricks()
    object Available : Biometricks()
    object NoneEnrolled : Biometricks()
}