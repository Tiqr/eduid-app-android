package nl.eduid.screens.oauth


import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import timber.log.Timber


class OAuthContract(
) : ActivityResultContract<OAuthViewModel, Intent?>() {

    override fun createIntent(context: Context, viewModel: OAuthViewModel): Intent = try {
        viewModel.createAuthorizationIntent()
    } catch (e: Exception) {
        Timber.e(e, "Failed to create authorization request intent")
        Intent()
    }


    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
        return intent
    }
}