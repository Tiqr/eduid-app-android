package nl.eduid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.ui.EduIdTopAppBar
import nl.eduid.ui.InfoField
import nl.eduid.ui.PrimaryButton
import nl.eduid.ui.SecondaryButton
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.ColorScale_Gray_100
import nl.eduid.ui.theme.ColorScale_Gray_300
import nl.eduid.ui.theme.ColorScale_Gray_400
import nl.eduid.ui.theme.ColorScale_Gray_500
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.ColorSupport_Blue_400
import nl.eduid.ui.theme.EduidAppAndroidTheme


/***
 * From designs:
 * Primary Button Full color
 * 	White on MainGreen 400
 * Disabled primary button:
 *  container = Grayscale 100
 *  text = Grayscale 300

 * Secondary Button Outline
 * 	Text Grayscale 500
 * 	Outline Grayscale 400
 *
 * Input field Label: Grayscale black
 * Input outline: Grayscale 400
 * Input color: Grayscale black
 *
 * Body text color: Grayscale 500
 * Body bold text blue highlight: Support Blue 400
 *
 * Highlighted green title/labels: Main Color green 400
 *
 * Navigation icon Support Blue 400
 *
 */
@Preview
@Composable
fun ThemeSample() = EduidAppAndroidTheme(colorScheme = LightColorsTest) {
    EduIdTopAppBar {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Body text is ColorGrayScale500", fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(text = "Primary", onClick = {})
                PrimaryButton(text = "Primary DISABLED", onClick = {}, enabled = false)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(
                    text = "Delete",
                    onClick = {},
                    buttonBackgroundColor = ColorAlertRed
                )
                PrimaryButton(
                    text = "Delete DISABLED",
                    onClick = {},
                    buttonBackgroundColor = ColorAlertRed,
                    enabled = false
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton(text = "Secondary", onClick = {})
                SecondaryButton(text = "Secondary DISABLED", onClick = {}, enabled = false)
            }

            InfoField(title = "Support Blue 400", subtitle = "Grayscale 500 ")

            HorizontalDivider(thickness = 4.dp)

            OutlinedTextField(
                value = "GrayScaleBlack: Input",
                onValueChange = {},
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = ColorScale_Gray_Black,
                    unfocusedTextColor = ColorScale_Gray_Black,
                )
            )
            OutlinedTextField(
                value = "", onValueChange = {},
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedPlaceholderColor = ColorScale_Gray_400,
                    unfocusedPlaceholderColor = ColorScale_Gray_400
                ),
                placeholder = {
                    Text(text = "GrayScale400: With placeholder & no label: NOT USED")
                },
            )
            OutlinedTextField(
                value = "", onValueChange = {},
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),

                label = {
                    Text(text = "GrayScale400: With label, no placeholder")
                },

                )

        }
    }
}

private val LightColorsTest = lightColorScheme(
    /**
     * Used by:
     * 1 - DEFAULT: container color in TopAppBar
     * 2 - DEFAULT: ListItem container color
     * */
    surface = Color.White,
    /**
     * Used by:
     *  1 - ✅DEFAULT: top appbar back navigation icon
     *  2 - ❌DEFAULT disabled primary button container (label with opacity 0.38f), REQUIRED:  container = Grayscale_100/tertiaryContainer, label = Grayscale_300/onTertiaryContainer
     *  3 - ❌DEFAULT disabled secondary button label (label with opacity 0.38f), REQUIRED?: label = Grayscale_300/onTertiaryContainer (making it consistent with primary button)
     *  4 - ✅DEFAULT: headline content color in ListItem
     *  5 - ❌DEFAULT input field color, REQUIRED: GrayscaleBlack
     * */
    onSurface = ColorSupport_Blue_400,

    /**
     * Used by:
     * 1 - DEFAULT: Scaffold containerColor ==> contentColor = contentColorFor(containerColor)
     * */
    background = Color.White,
    /**
     * Used by:
     * 1 - DEFAULT: Scaffold contentColor contentColorFor(containerColor)
     * */
    onBackground = ColorScale_Gray_500,

    /**
     * Used by:
     * 1 - ✅DEFAULT: supportingColor in List Item
     * 2 - ❌DEFAULT: trailing icon tint in List Item, but REQUIRED: ColorSupportBlue400/primary
     * 3 - ❌DEFAULT: Placeholder color for outlined input fields, but required Grayscale_400/outline
     * */
    onSurfaceVariant = ColorScale_Gray_500,

    /**
     * @Deprecated
     * Used by:
     * 1 - ✅DEFAULT: Primary button background
     * 2 - ❌DEFAULT: Secondary button label, but REQUIRED? label = Grayscale_500/onBackground
     * 3 - ❌DEFAULT: Input field FOCUSED outline color, but REQUIRED? label = ColorSupportBlue400/primary
     * 4 - ❌DEFAULT: Input field caret color, but REQUIRED? label = ColorSupportBlue400/primary
     * */
//    primary = ColorMainGreen400,
//Alternatively, make primary blue to avoid all the input issues?
    /**
     * Used by:
     * 1 - ❌DEFAULT: Primary button background, REQUIRED ColorMainGreen400/onSecondary
     * 2 - ❌DEFAULT: Secondary button label, but REQUIRED? label = Grayscale_500/onSurfaceVariant
     * 3 - ✅DEFAULT: Input field FOCUSED outline color
     * 4 - ✅DEFAULT: Input field caret color
     * */

    primary = ColorSupport_Blue_400,
    /**
     * Explicitly to be set for field input text color & label
     * */
    onPrimaryContainer = ColorScale_Gray_Black,
    /**
     * Used by:
     * 1 - ✅DEFAULT: Secondary button outline
     * */
    outline = ColorScale_Gray_400,
    /**
     * Used by:
     * 1 - ✅Default: Divider colors
     * */
    outlineVariant = ColorSupport_Blue_400,

    /**
     * Do not set, otherwise onSecondary replaces onBackground color (main font color in body)
     * */
//    secondary = Color.White,
    /**
     * 1 - Explicitly set for primary button container
     * 2 - Explicitly set for text highlights
     * */
    onSecondary = ColorMain_Green_400,
    /**
     * 1 - Explicitly set for disabled buttons container and onContainer color
     * */
    tertiaryContainer = ColorScale_Gray_100,
    onTertiaryContainer = ColorScale_Gray_300,


    )