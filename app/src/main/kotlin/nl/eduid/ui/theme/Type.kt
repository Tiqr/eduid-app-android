package nl.eduid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import nl.eduid.R

val proximaNovaSoftFamily = FontFamily(
    Font(R.font.proxima_nova_soft_semibold, FontWeight.SemiBold),
)
val sourceSansProFamily = FontFamily(
    Font(R.font.source_sans_pro_regular, FontWeight.Normal),
    Font(R.font.source_sans_pro_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.source_sans_pro_semibold, FontWeight.SemiBold),
    Font(R.font.source_sans_pro_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.source_sans_pro_bold, FontWeight.Bold),
    Font(R.font.source_sans_pro_bold_italic, FontWeight.Bold, FontStyle.Italic),
)

// Set of Material typography styles to start with
val Typography = Typography(
    labelSmall = TextStyle(
        fontFamily = sourceSansProFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = sourceSansProFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    // Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = proximaNovaSoftFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.18).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = proximaNovaSoftFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = proximaNovaSoftFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.18).sp,
    ),
    bodySmall = TextStyle(
        fontFamily = sourceSansProFamily,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = TextStyle(
        fontFamily = sourceSansProFamily,
        fontSize = 14.sp,
        lineHeight = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = sourceSansProFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
)