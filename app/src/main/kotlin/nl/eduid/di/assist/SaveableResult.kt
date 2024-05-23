package nl.eduid.di.assist

/**
 * Type used to represent editable network responses - i.e. the notification settings.
 * 1. [LoadError] : used to indicate if fetching the data from the network has failed.
 * 2. [Success] : used to show the data from the network or if a save error has happened. If data fetch was OK, it will be in [Success.data].
 * If an error happened during save it will be in [Success.saveError] and the last known state of the settings after edit will be available
 * in the [Success.data].
 *
 * Using the standard kotlin Result.success & Result.failure is enough for handling network data fetch. Those can indeed only succeed or fail
 * making that mapping both sufficient and complete.
 * However, because we have editable data in the settings screen, if an error happens during save, we cannot make the data we previously had
 * disappear of the screen. This type is meant to solve this issue.
 * */
sealed interface SaveableResult<out T> {
    data class Success<T>(val data: T, val saveError: Throwable? = null) : SaveableResult<T>

    data class LoadError(val exception: Throwable) : SaveableResult<Nothing>
}