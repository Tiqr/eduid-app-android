package nl.eduid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.eduid.graph.FlagsGraph
import nl.eduid.ui.theme.EduidAppAndroidTheme

@AndroidEntryPoint
class TestSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduidAppAndroidTheme {
                FlagsGraph(navController = rememberNavController())
            }
        }
    }
}