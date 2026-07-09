package nl.eduid

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.eduid.env.EnvironmentProvider
import org.tiqr.data.repository.NotificationCacheRepository
import org.tiqr.data.repository.NotificationData
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val notificationCacheRepository: NotificationCacheRepository,
    environmentProvider: EnvironmentProvider,
) : ViewModel() {
    val baseUrl = environmentProvider.getCurrent().baseUrl
    val environmentName = environmentProvider.getCurrent().name

    private val _isFcmTokenMissing = MutableStateFlow(false)
    val isFcmTokenMissing = _isFcmTokenMissing.asStateFlow()

    fun getLastNotificationChallenge(context: Context): NotificationData? {
        return notificationCacheRepository.getLastNotificationChallenge(context)
    }

    fun checkFcmToken() = viewModelScope.launch {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful || task.result == null) {
                _isFcmTokenMissing.value = true
            }
        }
    }

    fun clearFcmTokenMissing() {
        _isFcmTokenMissing.value = false
    }
}
