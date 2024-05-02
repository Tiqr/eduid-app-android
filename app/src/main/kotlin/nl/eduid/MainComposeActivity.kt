package nl.eduid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.eduid.graphs.MainGraph
import nl.eduid.ui.theme.EduidAppAndroidTheme
import timber.log.Timber

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EduidAppAndroidTheme {
                MainGraph(navController = rememberNavController())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent != null && intent.action == Intent.ACTION_VIEW) {
            Timber.d("Intent captured by MainComposeActivity ${this.hashCode()}: Received: ${intent.dataString}.")
        }
    }
}
