package nl.eduid.di.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import nl.eduid.di.repository.StorageRepository
import timber.log.Timber
import java.util.*
import kotlin.NoSuchElementException

class TokenProvider(private val repository: StorageRepository) {
    private var token: String? =
        "eyJraWQiOiJrZXlfMjAyM18wM18wMl8wMF8wMF8wMF8wMDAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOlsiZGV2LmVnZW5pcS5ubCIsIm15Y29uZXh0LnJzIl0sInN1YiI6ImRldi5lZ2VuaXEubmwiLCJuYmYiOjE2Nzc3NzMxNjMsInNjb3BlIjoiZWR1aWQubmxcL21vYmlsZSIsImlzcyI6Imh0dHBzOlwvXC9jb25uZWN0LnRlc3QyLnN1cmZjb25leHQubmwiLCJjbGFpbXMiOiJBUTkxeUt4S3UrOEFjcFlhd0lYdTVwK1lFK01RSFpocTJiUVBMYlZMTE9IWmt0dUJBMDFTNXZ4M2dtVmpzdllldFYyMFVtVGJFMDl6SjMzSENKNk1kVjE3SWxGSDdiK1ZpMUkyKzBFQThWVFdWdlwvQkpmcnkxcFBLVGJ4dWdMbFNaN0tHRGdzamp3VU9DWW8wS3JmTmYxK0VZVUMyeWIxdXV0a0lFWWx1WHZRT2RCY1JicjRCellpbkFSMUY3Y0FcL2swVUlWSFpSRU9wVjRCejNuODc0SWZxOFJZT2NsaldkUjF2XC9cL0V3Q00zUDRCM2s4dFRLVnBzaU13Y0tjVlZ6dWhIWkZaZHFONVhUMWZXSHhNMW1RekFoQVpJdnFSbXpYOGx5MFEzTXU4ZEdQSE9SS1h5djgzUkFBRDBXbmpFYWhUXC9zNUppWnowUEtZdm9xSmV0WTZieUF5cVdMQUwzMDVDOUpHaXRCZGxtdjdvcWFWczJDeFNnSGhydFNSQWZSSXAxQURyU2oxWktLaVBBS3NuMDNuZ3duK250d1wvRnBVVU1kQ1Y3SU95QWtxS2lHREJsVUJ4aDlNK0N6M3V5TUtiYmlnUVZHV1pzMUZONmt3b01oTm43MllQbzRCZjBLNnNqVll5VUVzMWlpUUJNckZxS01LQlZFa3V3MkZTV2RvMXVyYmdYKzFsQTZROFpkQlNORlE3WnVPOWU5Q1E5MHhDS2tDV1NjbyswcFdBbHFRWWhLaVEyOHBNOWphblwvcWxsVjJST2dmaE45a0JqejllaldKUUpJK0x1V3kzZUxSQmcyalwvcWlrZmt6elJvRVwvb21Ba1VSOUsxbHNzNTZ6Rm9pUUtSeUZySTBtU0xcL2tHQnhERmR5anBHa3J5Z0FLRUtLVUsyaiIsImV4cCI6MTY3Nzc3Njc2MywiY2xhaW1fa2V5X2lkIjoiMjU5Mzc3MzI0IiwiaWF0IjoxNjc3NzczMTYzLCJqdGkiOiI0YWJmOGRmNy02MzQxLTQ3NjUtYTQyNy1iOTU3MzY5YTRkN2MifQ.V3x94XrR_xBWejsJwlU0HTEO7AejEd770DJoH1iSfIk1ChVgdaZU1XX1BZzPji8pVEuB22aJCps5EAoMX5aNP3qkF7r7FDDqdhsRdT-2n855uOeT_6VGQ10caz5wO4HFOxSUZRn-9EJOfQNb-FYLI8ZlqdGy6JpjCBrHOKzRgFYjQjMmTM2zbx6ahyLk4bj_STU-jPnDgPvL4L05UcuR1WwBfGkcnVOpteZSnb4Hm64ibD9Zp7YdAVPSI39iHoq-z3ZieIbryZbu4FCUdkv0dgi0Oo3ueb0mTD14K2YCdOBw15SbfLertnS8L-aNg08cx-6xcp0S2VXE0K7dcJD_1g"

    suspend fun getToken(): String? = token ?: refreshToken()

    suspend fun refreshToken(): String? {
        token = if (!repository.isAuthorized.first()) {
            Timber.e("Not authorized. Token missing")
            null
        } else {
            try {
                //Todo: to an actual token refresh here
                val authState = repository.authState.firstOrNull()
                val expiresAt = Date(authState?.accessTokenExpirationTime ?: 0)
                val accessToken = authState?.accessToken
                Timber.e(
                    "Is authorized. token is available: $accessToken, expires at $expiresAt"
                )
                accessToken
            } catch (ex: NoSuchElementException) {
                Timber.w(ex, "Error refreshing token")
                null
            }
        }
        return token

    }
}