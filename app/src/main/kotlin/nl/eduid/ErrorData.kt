package nl.eduid

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class ErrorData(
    val title: String = "",
    val message: String = "",
    @StringRes private val titleId: Int = 0,
    @StringRes private val messageId: Int = 0,
    private val messageArg: String? = null,
) : Parcelable {
    fun title(context: Context): String = title.ifEmpty { context.getString(titleId) }
    fun message(context: Context): String =
        message.ifEmpty {
            if (messageArg == null) {
                context.getString(messageId)
            } else {
                context.getString(messageId, messageArg)
            }

        }

}