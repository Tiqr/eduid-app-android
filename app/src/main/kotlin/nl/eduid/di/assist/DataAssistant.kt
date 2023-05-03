package nl.eduid.di.assist

import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.SelfAssertedName
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

    suspend fun removeService(serviceId: String): UserDetails? = try {
        infoRepository.removeService(serviceId)
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    suspend fun getInstitutionName(schacHome: String): String? = try {
        infoRepository.getInstitutionName(schacHome)
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    suspend fun removeConnection(linkedAccount: LinkedAccount): UserDetails? = try {
        infoRepository.removeConnection(linkedAccount)
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    suspend fun getStartLinkAccount(): String? = try {
        infoRepository.getStartLinkAccount()
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    suspend fun updateName(selfAssertedName: SelfAssertedName): UserDetails? = try {
        infoRepository.updateName(selfAssertedName)
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }
}