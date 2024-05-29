package nl.eduid.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    surface = Color.White,
    onSurface = ColorSupport_Blue_400,
    background = Color.White,
    onBackground = ColorScale_Gray_500,
    onSurfaceVariant = ColorScale_Gray_500,
    primary = ColorSupport_Blue_400,
    onPrimaryContainer = ColorScale_Gray_Black,
    outline = ColorScale_Gray_400,
    outlineVariant = ColorSupport_Blue_400,
    onSecondary = ColorMain_Green_400,
    tertiaryContainer = ColorScale_Gray_100,
    onTertiaryContainer = ColorScale_Gray_300,
    
)

@Composable
fun EduidAppAndroidTheme(
    colorScheme: ColorScheme = LightColors,
    content: @Composable () -> Unit,
) = MaterialTheme(
    colorScheme = colorScheme, typography = Typography, content = content
)

tailrec fun Context.findActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> this.baseContext.findActivity()
    else -> throw IllegalArgumentException("Could not find activity!")
}
