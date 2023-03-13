package nl.eduid.screens.created

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.EduIdTopAppBar

@Composable
fun RequestEduIdCreatedScreen(
    isCreated: Boolean,
    onBackClicked: () -> Unit
) {
    EduIdTopAppBar(
        onBackClicked = {},
        withBackIcon = false,
    ) {
        Text(
            text = stringResource(R.string.enroll_screen_title),
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )

    }
}