package nl.eduid.di.assist

import nl.eduid.di.model.UnauthorizedException
import nl.eduid.di.model.UserDetails
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import javax.inject.Inject

class DataAssistant @Inject constructor(
    private val infoRepository: PersonalInfoRepository,
    private val storageRepository: StorageRepository
) {
    suspend fun getErringUserDetails(): UserDetails? = try {
        infoRepository.getErringUserDetails()
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    suspend fun removeService(serviceId: String): UserDetails? =
        infoRepository.removeService(serviceId)
}