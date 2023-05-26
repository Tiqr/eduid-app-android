package nl.eduid.screens.dataactivity

data class ServiceProvider(
    val providerName: String,
    val createdStamp: Long,
    val firstLoginStamp: Long,
    val uniqueId: String,
    val serviceProviderEntityId: String,
    val providerLogoUrl: String?,
)