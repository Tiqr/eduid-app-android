package nl.eduid.di.assist

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import nl.eduid.di.model.IdpScoping
import nl.eduid.di.model.LinkedAccount
import nl.eduid.di.model.LinkedAccountUpdateRequest
import nl.eduid.di.model.SelfAssertedName
import nl.eduid.di.model.TokenResponse
import nl.eduid.di.model.UserDetails
import nl.eduid.di.repository.StorageRepository
import nl.eduid.screens.personalinfo.PersonalInfoRepository
import org.tiqr.data.di.DefaultDispatcher
import timber.log.Timber
import javax.inject.Inject

class DataAssistant
@Inject
constructor(
    private val infoRepository: PersonalInfoRepository,
    private val storageRepository: StorageRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {
    private val cachedDetails = MutableStateFlow<SaveableResult<UserDetails>?>(null)
    val observableDetails: Flow<SaveableResult<UserDetails>?> = cachedDetails.map { knownValue ->
        if (knownValue == null) {
            val fromNetwork = try { loadInCache() } catch (ex: Exception) { return@map handleError(ex) }
            fromNetwork.fold(
                onSuccess = {
                    SaveableResult.Success(it)
                },
                onFailure = {
                    handleError(it)
                },
            )
        } else {
            knownValue
        }
    }

    suspend fun refreshDetails() = withContext(dispatcher) {
        val fromNetwork = loadInCache()
        cachedDetails.emit(
            fromNetwork.fold(
                onSuccess = { SaveableResult.Success(it) },
                onFailure = { handleError(it) }
            )
        )
        return@withContext fromNetwork.getOrNull()
    }

    private suspend fun handleError(ex: Throwable): SaveableResult.LoadError {
        return if (ex is UnauthorizedException) {
            storageRepository.clearInvalidAuth()
            SaveableResult.LoadError(ex)
        } else {
            SaveableResult.LoadError(DataFetchException("Failed to get user notification settings", ex))
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

    suspend fun getTokensForUser(): List<TokenResponse>? = try {
        infoRepository.getTokensForUser()
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

    suspend fun updateTokens(revokeToken: TokenResponse): UserDetails? = try {
        infoRepository.revokeToken(revokeToken)
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

    suspend fun removeConnection(subjectId: String) = withContext(dispatcher) {
        val currentDetails = currentCachedSettings() ?: fetchDetails()
        val linkedAccount =
            currentDetails?.linkedAccounts?.firstOrNull { it.subjectId == subjectId }
        linkedAccount?.let {
            val updateRequest = LinkedAccountUpdateRequest(
                eduPersonPrincipalName = it.eduPersonPrincipalName,
                subjectId = it.subjectId,
                external = false,
                idpScoping = null
            )
            val updatedDetails = infoRepository.removeConnectionResult(updateRequest)
            forwardWithFallback(updatedDetails, currentDetails, "Remove connection")
        }
        val externalAccount = currentDetails?.externalLinkedAccounts?.firstOrNull { it.subjectId == subjectId }
        externalAccount?.let {
            val updateRequest = LinkedAccountUpdateRequest(
                eduPersonPrincipalName = null,
                subjectId = it.subjectId,
                external = true,
                idpScoping = it.idpScoping
            )
            val updatedDetails = infoRepository.removeConnectionResult(updateRequest)
            forwardWithFallback(updatedDetails, currentDetails, "Remove external connection")
        }
    }

    suspend fun getStartLinkAccount(): String? = withContext(dispatcher) {
        val result = infoRepository.getStartLinkAccountResult()
        result.fold(
            onSuccess = {
                it?.url
            },
            onFailure = {
                storageRepository.clearInvalidAuth()
                null
            },
        )
    }

    suspend fun preferLinkedAccount(request: LinkedAccountUpdateRequest) = withContext(dispatcher) {
        if (infoRepository.preferLinkedAccount(request)) {
            // Also refresh the details, if it was a success, so we have the latest data
            refreshDetails()
            true
        } else {
            false
        }
    }

    suspend fun getExternalAccountLinkResult(idpScoping: IdpScoping, bankId: String?): String? = withContext(dispatcher) {
        val result = infoRepository.getExternalAccountLinkResult(idpScoping, bankId)
        result.fold(
            onSuccess = {
                it?.url
            },
            onFailure = {
                storageRepository.clearInvalidAuth()
                null
            },
        )
    }

    suspend fun updateName(selfAssertedName: SelfAssertedName): UserDetails? = try {
        infoRepository.updateName(selfAssertedName)
    } catch (e: UnauthorizedException) {
        storageRepository.clearInvalidAuth()
        throw e
    }

    private suspend fun forwardWithFallback(result: Result<UserDetails>, knownDetails: UserDetails, operation: String = "") =
        result.fold(
            onSuccess = {
                Timber.e("Forwarding new details")
                cachedDetails.emit(SaveableResult.Success(it))
            },
            onFailure = {
                Timber.e(it, "Failed to perform $operation")
                cachedDetails.emit(
                    SaveableResult.Success(knownDetails, OperationFailException(operation, null, it)),
                )
            },
        )

    private suspend fun currentCachedSettings(): UserDetails? = when (val current = cachedDetails.firstOrNull()) {
        is SaveableResult.LoadError -> null
        is SaveableResult.Success -> current.data
        null -> null
    }

    private suspend fun fetchDetails(): UserDetails? = withContext(dispatcher) {
        val result = infoRepository.getUserDetailsResult()
        result.fold(onSuccess = {
            it
        }, onFailure = {
            null
        })
    }
}