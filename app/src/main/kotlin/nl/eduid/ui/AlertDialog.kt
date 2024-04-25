package nl.eduid.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import nl.eduid.R

@Composable
fun AlertDialogWithSingleButton(
    title: String,
    explanation: String,
    buttonLabel: String,
    onDismiss: () -> Unit = {},
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
            onDismiss()
        }, title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        }, text = {
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
            )
        }, confirmButton = {}, dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
                onDismiss()
            }) {
                Text(
                    text = buttonLabel,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        })
    }
}

@Composable
fun AlertDialogWithTwoButton(
    title: String,
    explanation: String,
    dismissButtonLabel: String,
    confirmButtonLabel: String,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openDialog.value = false
            onDismiss()
        }, title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        }, text = {
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
            )
        }, confirmButton = {
            TextButton(onClick = {
                openDialog.value = false
                onConfirm()
            }) {
                Text(
                    text = confirmButtonLabel,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }, dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
                onDismiss()
            }) {
                Text(
                    text = dismissButtonLabel,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        })
    }
}

@Composable
fun DeleteServiceDialog(
    service: String,
    explanation: String,
    dismissButtonLabel: String,
    confirmButtonLabel: String,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
            onDismiss()
        }, title = {
            TwoColorTitle(
                firstPart = stringResource(id = R.string.DeleteService_Title_COPY),
                secondPart = "$service?"
            )
        }, text = {
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
            )
        }, confirmButton = {
            TextButton(onClick = {
                openDialog.value = false
                onConfirm()
            }) {
                Text(
                    text = confirmButtonLabel,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }, dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
                onDismiss()
            }) {
                Text(
                    text = dismissButtonLabel,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        })
    }
}