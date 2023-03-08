package nl.eduid

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import nl.eduid.di.auth.TokenProvider
import javax.inject.Inject

class ApplicationLifecycleObserver @Inject constructor(private val tokenProvider: TokenProvider) :
    DefaultLifecycleObserver {

    //Must call dispose on the [AuthorizationService] when it's being used in the singleton [TokenProvider]
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        tokenProvider.disposeService()
    }
}