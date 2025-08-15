package nl.eduid.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiError(
    val error: String?,
    val exception: String?,
    val message: String?
)