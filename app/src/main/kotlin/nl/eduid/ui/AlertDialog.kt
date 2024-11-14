package nl.eduid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import nl.eduid.R
import nl.eduid.ui.theme.ColorAlertRed
import nl.eduid.ui.theme.ColorMain_Green_400
import nl.eduid.ui.theme.ColorScale_Gray_500
import nl.eduid.ui.theme.ColorScale_Gray_Black
import nl.eduid.ui.theme.EduidAppAndroidTheme

@Composable
fun AlertDialogWithSingleButton(title: String, explanation: String, buttonLabel: String, onDismiss: () -> Unit = {}) {
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
    isDestroyAction: Boolean,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        Box( // Box for scrim
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.2f))
                .fillMaxSize()
        ) {
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
                PrimaryButton(
                    onClick = {
                        openDialog.value = false
                        onConfirm()
                    },
                    text = confirmButtonLabel,
                    buttonBackgroundColor = if (isDestroyAction) ColorAlertRed else ColorMain_Green_400
                )
            }, dismissButton = {
                SecondaryButton(
                    onClick = {
                        openDialog.value = false
                        onDismiss()
                    },
                    text = dismissButtonLabel,
                )
            },
                containerColor = Color.White,
                titleContentColor = ColorScale_Gray_Black,
                textContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun DeleteServiceDialog(service: String, onDismiss: () -> Unit = {}, onConfirm: () -> Unit = {}) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
            onDismiss()
        }, title = {
            TwoColorTitle(
                firstPart = stringResource(id = R.string.DeleteService_Title_COPY),
                secondPart = "$service?",
            )
        }, text = {
            Text(
                text = stringResource(id = R.string.DeleteService_Description_COPY),
                style = MaterialTheme.typography.bodyLarge.copy(color = ColorScale_Gray_500),
            )
        }, confirmButton = {
            PrimaryButton(
                text = stringResource(id = R.string.DeleteService_Button_Confirm_COPY),
                buttonBackgroundColor = ColorAlertRed,
                onClick = {
                    openDialog.value = false
                    onConfirm()
                },
            )
        }, dismissButton = {
            SecondaryButton(
                text = stringResource(id = R.string.Button_Cancel_COPY),
                onClick = {
                    openDialog.value = false
                    onDismiss()
                },
            )
        })
    }
}

@Composable
fun RevokeTokenDialog(token: String, onDismiss: () -> Unit = {}, onConfirm: () -> Unit = {}) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
            onDismiss()
        }, title = {
            TwoColorTitle(
                firstPart = stringResource(id = R.string.RevokeAccessToken_Title_COPY),
                secondPart = "",
            )
        }, text = {
            Text(
                text = stringResource(id = R.string.RevokeAccessToken_Description_COPY, token),
                style = MaterialTheme.typography.bodyLarge.copy(color = ColorScale_Gray_500),
            )
        }, confirmButton = {
            PrimaryButton(
                text = stringResource(id = R.string.RevokeAccessToken_Button_Confirm_COPY),
                buttonBackgroundColor = ColorAlertRed,
                onClick = {
                    openDialog.value = false
                    onConfirm()
                },
            )
        }, dismissButton = {
            SecondaryButton(
                text = stringResource(id = R.string.RevokeAccessToken_Button_Cancel_COPY),
                onClick = {
                    openDialog.value = false
                    onDismiss()
                },
            )
        })
    }
}

@Preview
@Composable
private fun Preview_DeleteService() = EduidAppAndroidTheme {
    DeleteServiceDialog(
        service = "service",
    )
}

@Preview
@Composable
private fun Preview_RevokeToken() = EduidAppAndroidTheme {
    RevokeTokenDialog(token = "eduid mobile app")
}