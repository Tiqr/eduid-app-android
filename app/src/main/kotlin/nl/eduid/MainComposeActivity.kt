package nl.eduid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.eduid.graphs.MainGraph
import nl.eduid.messaging.EduIdMessagingService.Companion.SERVICE_NAME
import nl.eduid.ui.theme.EduidAppAndroidTheme
import org.tiqr.data.util.GooglePlayServicesUtil
import org.tiqr.data.util.InAppUpdatesUtil
import timber.log.Timber

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {

    internal val viewModel by viewModels<ActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val isFcmTokenMissing by viewModel.shouldInformFCMDisabled.collectAsStateWithLifecycle()

            EduidAppAndroidTheme {
                val okButton = stringResource(R.string.Button_OK_COPY)
                val message = stringResource(R.string.UseApp_NotificationsNotPossible_COPY)
                LaunchedEffect(isFcmTokenMissing) {
                    if (isFcmTokenMissing) {
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = okButton,
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    MainGraph(navController = rememberNavController(), baseUrl = viewModel.baseUrl)
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .systemBarsPadding()
                            .padding(bottom = 8.dp)
                    ) { data ->
                        Snackbar(
                            modifier = Modifier
                                .padding(12.dp), action = {
                                TextButton(
                                    onClick = {
                                        viewModel.clearFcmTokenMissing()
                                        data.performAction()
                                    },
                                ) { Text(data.visuals.actionLabel ?: "") }
                            }) {
                            Text(data.visuals.message)
                        }
                    }
                }
            }
        }

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            InAppUpdatesUtil.checkForUpdates(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent != null && intent.action == Intent.ACTION_VIEW) {
            Timber.d("Intent captured by MainComposeActivity ${this.hashCode()}: Received: ${intent.dataString}.")
        }
        if (intent?.dataString == null) {
            viewModel.getLastNotificationChallenge(this)?.let { notificationData ->
                val newIntent = Intent(Intent.ACTION_VIEW, notificationData.challenge.toUri())
                newIntent.putExtra(SERVICE_NAME, notificationData.serviceName)

                startActivity(newIntent)
            }
        }

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            viewModel.checkFcmToken()
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(
                this,
                getString(R.string.environment_info_toast, viewModel.environmentName),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}