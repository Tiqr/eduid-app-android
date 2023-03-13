package nl.eduid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.eduid.graphs.MainGraph
import nl.eduid.screens.homepage.HomePageViewModel
import nl.eduid.screens.splash.SplashScreen
import nl.eduid.ui.theme.EduidAppAndroidTheme
import timber.log.Timber

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {
    private val viewModel by viewModels<HomePageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            EduidAppAndroidTheme {
                val knownState by viewModel.knownState.observeAsState(initial = null)
                val navController = rememberNavController()
                if (knownState == null) {
                    SplashScreen()
                } else {
                    MainGraph(navController = navController, viewModel)
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        if (intent != null && intent.action == Intent.ACTION_VIEW) {
            intent.dataString?.let { rawChallenge ->
                Timber.e("Compose activity resumed, Intent data: $rawChallenge")
                intent.data = null
            }
        }
    }

}
