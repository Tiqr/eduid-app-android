package nl.eduid.screens.dataactivity

data class DataAndActivityData(
    val providerList: List<Provider>? = null,
) {
    data class Provider(
        val providerName: String,
        val createdStamp: Long,
        val firstLoginStamp: Long,
        val uniqueId: String,
        val serviceProviderEntityId: String,
        val providerLogoUrl: String,
    )
}