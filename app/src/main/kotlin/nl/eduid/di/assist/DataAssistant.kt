package nl.eduid.di.assist

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.UnauthorizedException
import nl.eduid.di.model.UserDetails
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import org.tiqr.data.di.DefaultDispatcher
import javax.inject.Inject

class DataAssistant @Inject constructor(
    private val infoRepository: PersonalInfoRepository,
    private val storageRepository: StorageRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {
    private val cachedDetails = MutableStateFlow<SaveableResult<UserDetails>?>(null)
    val observableDetails: Flow<SaveableResult<UserDetails>?> = cachedDetails.map { knownValue ->
        if (knownValue == null) {
            val fromNetwork = loadInCache()
            fromNetwork.fold(
                onSuccess = {
                    SaveableResult.Success(it)
                },
                onFailure = {
                    if (it is UnauthorizedException) {
                        storageRepository.clearInvalidAuth()
                        SaveableResult.LoadError(it)
                    } else {
                        SaveableResult.LoadError(
                            DataFetchException(
                                "Failed to get user notification settings", it
                            )
                        )
                    }
                },
            )
        } else {
            knownValue
        }

    }

    private suspend fun loadInCache(): Result<UserDetails> = withContext(dispatcher) {
        infoRepository.getUserDetailsResult()
    }

    suspend fun changeEmail(newEmail: String): Int? = try {
        infoRepository.changeEmail(newEmail)
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    suspend fun getErringUserDetails(): UserDetails? = try {
        infoRepository.getErringUserDetails()
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    suspend fun confirmEmail(confirmEmailHash: String): UserDetails? = try {
        infoRepository.confirmEmailUpdate(confirmEmailHash)
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