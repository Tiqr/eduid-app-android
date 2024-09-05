package nl.eduid.messaging

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nl.eduid.R
import org.tiqr.data.repository.NotificationCacheRepository
import org.tiqr.data.repository.base.TokenRegistrarRepository
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EduIdMessagingService : FirebaseMessagingService() {
    companion object {
        private const val MESSAGE_TEXT = "text"
        private const val MESSAGE_CHALLENGE = "challenge"
        private const val CHANNEL_ID = "default"
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    internal lateinit var tokenRegistrarRepository: TokenRegistrarRepository

    @Inject
    internal lateinit var notificationCacheRepository: NotificationCacheRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        scope.launch {
            tokenRegistrarRepository.registerDeviceToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.e("Received new message")
        sendNotification(message)
    }

    /**
     * Send the notification to the app to enroll or authenticate
     */
    private fun sendNotification(message: RemoteMessage) {
        val title = getString(R.string.app_name)
        val text = message.data[MESSAGE_TEXT]
        val challenge = message.data[MESSAGE_CHALLENGE]

        if (!challenge.isNullOrEmpty()) {
            val notificationManager = NotificationManagerCompat.from(this)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        resources.getString(org.tiqr.data.R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).also {
                        it.description =
                            resources.getString(R.string.notification_channel_description)
                    }
                )
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(challenge)).also { intent ->
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                //When using only FLAG_ACTIVITY_MULTIPLE_TASK a new separate task does not seem to be created for
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            }
            val flags =
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, flags))
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTimeoutAfter(180_000) // 3 minutes
                .setContentTitle(title)
                .setTicker(text)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(resources.getColor(R.color.primaryColor))
                .build()
                .apply {
                    val identifier = System.currentTimeMillis().toInt()
                    Timber.e("Sending notification with ID: $identifier")
                    val authenticationTimeout = message.data["authenticationTimeout"]?.toIntOrNull() ?: 150
                    notificationManager.notify(identifier, this)
                    notificationCacheRepository.saveLastNotificationData(challenge, authenticationTimeout, identifier)
                }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}