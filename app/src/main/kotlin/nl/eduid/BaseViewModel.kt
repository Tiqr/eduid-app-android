package nl.eduid

import androidx.lifecycle.ViewModel
import com.squareup.moshi.Moshi
import org.tiqr.data.model.AuthenticationChallenge
import org.tiqr.data.model.Challenge
import org.tiqr.data.model.EnrollmentChallenge
import java.net.URLEncoder

open class BaseViewModel(private val moshi: Moshi) : ViewModel() {

    fun encodeChallenge(scanResult: Challenge): String {
        val asJson: String = when (scanResult) {
            is EnrollmentChallenge -> {
                val adapter = moshi.adapter(EnrollmentChallenge::class.java)
                adapter.toJson(scanResult)
            }
            is AuthenticationChallenge -> {
                val adapter = moshi.adapter(AuthenticationChallenge::class.java)
                adapter.toJson(scanResult)
            }
        }
        return URLEncoder.encode(asJson, Charsets.UTF_8.toString())
    }

}