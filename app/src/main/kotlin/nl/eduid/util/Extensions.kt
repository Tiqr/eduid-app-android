package nl.eduid.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import nl.eduid.R
import nl.eduid.di.model.ExternalLinkedAccountIssuer
import nl.eduid.di.model.IdpScoping

@Composable
fun String.normalizedIssuerName(): String {
    if (this == IdpScoping.EHERKENNING.toString()) {
        return stringResource(R.string.ReferenceNames_Eherkenning_COPY)
    } else if (this == IdpScoping.IDIN.toString()) {
        return stringResource(R.string.ReferenceNames_Idin_COPY)
    } else {
        return this
    }
}