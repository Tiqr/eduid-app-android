package nl.eduid.screens.personalinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import timber.log.Timber

class LinkAccountContract : ActivityResultContract<Intent, Intent?>() {

    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
        Timber.Forest.d("Received callback from linked account: ${intent?.dataString}")
        return if (resultCode == Activity.RESULT_CANCELED) {
            null
        } else {
            intent
        }
    }
}