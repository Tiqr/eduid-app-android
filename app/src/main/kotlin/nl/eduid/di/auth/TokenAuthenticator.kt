package nl.eduid.di.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider
) : Authenticator {

    private var currentToken: String? =
        "eyJraWQiOiJrZXlfMjAyM18wM18wN18wMF8wMF8wMF8wMTYiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOlsiZGV2LmVnZW5pcS5ubCIsIm15Y29uZXh0LnJzIl0sInN1YiI6ImRldi5lZ2VuaXEubmwiLCJuYmYiOjE2NzgxNzI2MDYsInNjb3BlIjoiZWR1aWQubmxcL21vYmlsZSIsImlzcyI6Imh0dHBzOlwvXC9jb25uZWN0LnRlc3QyLnN1cmZjb25leHQubmwiLCJjbGFpbXMiOiJBUTkxeUt6RTRRTW5odTdkaXZuMGRlQ1wvMm5CaWY1M1Jpa1laVWFPWktiNkVaQWJnbzdZY1RKSFNiQTVWeVwvejg4Y3JOYmZSQmJscnNqNDk3eGc2cXhaNll2YStXNkdoOFpTXC8xakxRQ2QzV3E1b05hajlkZkFJQjVLWDRBXC9hSEhXUDN6M0wycmlUSitJTlF5Uk5BOTkrUDZ0UWFPSWd3aFwvYmRUcllRZEVNQU1GeXVwSkh1b0tDU0pZR2kwNDY3Nnp5aGNkeXVncU9WOG1nR3JvTVFWZExKdTBFRzJ3V0laSVhUUmlsbXJmdzhqYittaW5wS01mY0pRb1NQYWVLbVNJdWx1OUU3WW14YmI3Z09kMEpMN3BkY2lrZUFId1pvazBCdDg5XC8rcnNzNlFxbUxWQVRGa1Qyb0YzK0h6amowVGtmdFFIRW44OGZOS1BoUGQ4U1wvS25EQytpTDlpQ3BEZkdcL2xpWWw4SUJHYUNwRnpOWjROd2JxaEM3YnpiVkZiVEdSK3ZTa2JqRTlFZ00yXC9UZk9KckNRaDM0c1VKXC9nVUJOc1JnV1EyeUpYSkl5Rk9IZWtiR3gyV0o1WkUxZ1wvV0pzcENZYXRUTFhIQ2pvSU9YdkVRRWNmZlNYWlFWbThBb09hbG9OWEFuNDZ1UjBZRXdtNk9xekFkcjlVcEp1Tjg1VVlYZDcxMStCcFEyQW1FQ3BMMWJYXC8zaFlcL1lhZzNMQzZsV2xROERuS1VkXC9xUitJMmdmbzlaZmk2R1NqOVl3dWxhbHBrcjdSOWpnTWNYQTZlRWc0R0JUdEdGK1BGdlwvaFQxMXBLZ3RQWlZqeVA1dlI5KzRDOE1qY1NtU0s1bzg2eitqVkk3Y1RkSk1odjRRSFA2ZUF2aFRoc0dvRElLV2wwbjVIIiwiZXhwIjoxNjc4MTc2MjA2LCJjbGFpbV9rZXlfaWQiOiIyNTkzNzczMjQiLCJpYXQiOjE2NzgxNzI2MDYsImp0aSI6ImJkZWFmOTU2LWExZjUtNDFjZC1hMTVmLWY4MGZhMzM2ZDA0OSJ9.pXguHBZ8eor1CdNmWZKZaJW4jdeGQGrz6XIBZf13-ZUkr3HQu5KlH9RT4wIDF_3cnfmlqqE6tn5DOBG-4xdyVbfcbyEM4seckT55XcturQlrW-QXL9v6jq2jk5QAp0b8Foj7LYGc1pqPIaPQj_WaOBq8ddtrY8uqf4npapM_5nNe8w2z3Y6F3xQ4rkgfyJ1bKtupYJK7aGEBhbN75hkyV4TbkWh9Jwv2cBazxpLfUNbg05W32PFcrTyNIX5lslu-meCzSe6SKctLIjfFdQPpvZlPDXF_hoiyBW-r98SpQOf0CQBHyvYu4YubpQXXP5EvUkLZPM02zLJTUun2EIRAng"

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            Timber.e("Authenticator intercept. Running on: ${Thread.currentThread().name}")
            val previousToken = currentToken
            return response.request.newBuilder().header(
                "Authorization", "Bearer $currentToken"
            ).build()

//            return runBlocking(Dispatchers.IO) {
//                Timber.e("Blocking. Running on: ${Thread.currentThread().name}")
//                val token = tokenProvider.refreshToken()
//
//                if (token != previousToken) {
//                    currentToken = token
//                    response.request.newBuilder().header(
//                        "Authorization", "Bearer $token"
//                    ).build()
//                } else {
//                    null
//                }
//            }
        }
    }
}