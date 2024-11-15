package nl.eduid.di.model

import kotlinx.collections.immutable.toImmutableList
import nl.eduid.screens.personalinfo.PersonalInfo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

fun LinkedAccount.mapToInstitutionAccount(): PersonalInfo.InstitutionAccount? =
    this.eduPersonAffiliations.firstOrNull()?.let { affiliation ->
        //Just in case affiliation is not in the email format
        val role = if (affiliation.indexOf("@") > 0) {
            affiliation.substring(0, affiliation.indexOf("@"))
        } else {
            affiliation
        }
        PersonalInfo.InstitutionAccount(
            subjectId = this.subjectId ?: this.institutionIdentifier,
            role = role,
            roleProvider = this.schacHomeOrganization,
            institution = this.schacHomeOrganization,
            affiliationString = affiliation,
            givenName = this.givenName,
            familyName = this.familyName,
            createdStamp = this.createdAt,
            expiryStamp = this.expiresAt,
        )
    }

fun ExternalLinkedAccount.mapToInstitutionAccount(): PersonalInfo.InstitutionAccount? =
    PersonalInfo.InstitutionAccount(
        subjectId = this.subjectId ?: "",
        role = null,
        roleProvider = null,
        institution = this.issuer?.name ?: this.idpScoping ?: "",
        affiliationString = null,
        givenName = this.givenName,
        familyName = this.familyName,
        dateOfBirth = this.dateOfBirth?.let { LocalDate.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC) },
        createdStamp = this.createdAt,
        expiryStamp = this.expiresAt,
    )



fun UserDetails.mapToPersonalInfo(): PersonalInfo {
    val dateCreated = this.created * 1000
    val linkedAccounts = this.linkedAccounts

    val familyNameConfirmer = linkedAccounts.firstOrNull { it.familyName != null }
    val givenNameConfirmer = linkedAccounts.firstOrNull { it.givenName != null }

    val affiliationProvider = linkedAccounts.firstOrNull()
    val nameProvider = affiliationProvider?.schacHomeOrganization
    val name: String = affiliationProvider?.let {
        "${it.givenName} ${it.familyName}"
    } ?: "${this.chosenName} ${this.familyName}"

    val email: String = this.email
    val linkedInternalAccounts = linkedAccounts.mapNotNull { account ->
        account.mapToInstitutionAccount()
    }.toImmutableList()
    val linkedExternalAccounts = this.externalLinkedAccounts.mapNotNull { account ->
        account.mapToInstitutionAccount()
    }.toImmutableList()

    return PersonalInfo(
        name = name,
        selfAssertedName = SelfAssertedName(
            familyName = this.familyName,
            givenName = this.givenName,
            chosenName = this.chosenName
        ),
        confirmedName = ConfirmedName(
            familyName = familyNameConfirmer?.familyName,
            familyNameConfirmedBy = familyNameConfirmer?.institutionIdentifier,
            givenName = givenNameConfirmer?.givenName,
            givenNameConfirmedBy = givenNameConfirmer?.institutionIdentifier
        ),
        nameProvider = nameProvider,
        email = email,
        linkedInternalAccounts = linkedInternalAccounts,
        linkedExternalAccounts = linkedExternalAccounts,
        dateCreated = dateCreated,
    )
}