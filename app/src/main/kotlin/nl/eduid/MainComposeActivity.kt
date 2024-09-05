package nl.eduid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.eduid.graphs.MainGraph
import nl.eduid.ui.theme.EduidAppAndroidTheme
import timber.log.Timber

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {

    internal val viewModel by viewModels<ActivityViewModel>()
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EduidAppAndroidTheme {
                navController = rememberNavController().also {
                    MainGraph(navController = it)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent != null && intent.action == Intent.ACTION_VIEW) {
            Timber.d("Intent captured by MainComposeActivity ${this.hashCode()}: Received: ${intent.dataString}.")
        }
        if (intent?.dataString == null) {
            viewModel.getLastNotificationChallenge(this)?.let { challenge ->
                if (intent == null) {
                    intent = Intent()
                }
                intent.setData(Uri.parse(challenge))
                navController?.navigate(Uri.parse(challenge))
            }
        }
    }
}