package nl.eduid

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.eduid.env.EnvironmentProvider
import org.tiqr.data.repository.NotificationCacheRepository
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val notificationCacheRepository: NotificationCacheRepository,
    environmentProvider: EnvironmentProvider,
) : ViewModel() {
    val baseUrl = environmentProvider.getCurrent().baseUrl
    val environmentName = environmentProvider.getCurrent().name

    fun getLastNotificationChallenge(context: Context): String? {
        return notificationCacheRepository.getLastNotificationChallenge(context)
    }

}