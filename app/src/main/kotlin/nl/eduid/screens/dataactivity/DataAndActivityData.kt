package nl.eduid.screens.dataactivity

data class DataAndActivityData(
    val providerList: List<Provider>? = null,
) {
    companion object {
        data class Provider(
            val providerName: String,
            val createdStamp: Long,
            val firstLoginStamp: Long,
            val uniqueId: String,
            val providerLogoUrl: String,
        )
    }
}