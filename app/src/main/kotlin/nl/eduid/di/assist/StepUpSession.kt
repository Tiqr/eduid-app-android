package nl.eduid.di.assist

import okhttp3.Cookie
import okhttp3.Response
import timber.log.Timber

/**
 * Holds the cookies that bind the app to the server-side HTTP session created by `/mobile/tiqr/sp/start-authentication`.
 *
 * The backend stores the tiqr session key in that HTTP session, and the follow-up step-up calls (re-send and re-verify
 * phone code) read it back from there. As the app has no global cookie jar, the cookies are captured explicitly from
 * the start-authentication response and sent explicitly as a `Cookie` header on the follow-up calls only.
 */
class StepUpSession {
    private val lock = Any()
    private var cookies: Map<String, Cookie> = emptyMap()

    /** Starts a new step-up session from the start-authentication response, replacing any previous one. */
    fun start(response: Response): Boolean = synchronized(lock) {
        cookies = Cookie.parseAll(response.request.url, response.headers)
            .filter { it.expiresAt > System.currentTimeMillis() }
            .associateBy { it.name }
        Timber.d("Step-up session started with cookies: ${cookies.keys}")
        cookies.isNotEmpty()
    }

    /** Applies cookies (re)set by a follow-up response, e.g. when the server or load balancer rotates them. */
    fun update(response: Response) = synchronized(lock) {
        val received = Cookie.parseAll(response.request.url, response.headers)
        if (received.isEmpty()) return@synchronized
        val now = System.currentTimeMillis()
        cookies = (cookies + received.associateBy { it.name }).filterValues { it.expiresAt > now }
        Timber.d("Step-up session cookies updated: ${received.map { it.name }}, now holding: ${cookies.keys}")
    }

    /** The `Cookie` header value to send, or null when there is no active step-up session. */
    fun cookieHeader(): String? = synchronized(lock) {
        cookies.values.takeIf { it.isNotEmpty() }?.joinToString("; ") { "${it.name}=${it.value}" }
    }

    fun clear() = synchronized(lock) {
        if (cookies.isNotEmpty()) Timber.d("Step-up session cleared")
        cookies = emptyMap()
    }
}

/** Thrown when a step-up call is made without the session cookies from a successful start-authentication call. */
class MissingStepUpSessionException(message: String) : Exception(message)
