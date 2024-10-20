package nl.eduid.di.model

import com.squareup.moshi.Moshi
import nl.eduid.screens.personalinfo.PersonalInfo
import java.net.URLEncoder

fun LinkedAccount.mapToInstitutionAccount(asJson: String): PersonalInfo.InstitutionAccount? =
    this.eduPersonAffiliations.firstOrNull()?.let { affiliation ->
        //Just in case affiliation is not in the email format
        val role = if (affiliation.indexOf("@") > 0) {
            affiliation.substring(0, affiliation.indexOf("@"))
        } else {
            affiliation
        }
        PersonalInfo.InstitutionAccount(
            id = this.institutionIdentifier,
            linkedAccountJson = URLEncoder.encode(asJson, Charsets.UTF_8.toString()),
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


fun UserDetails.mapToPersonalInfo(moshi: Moshi): PersonalInfo {
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
    val adapter = moshi.adapter(LinkedAccount::class.java)
    val institutionAccounts = linkedAccounts.mapNotNull { account ->
        account.mapToInstitutionAccount(adapter.toJson(account))
    }

    return PersonalInfo(
        name = name,
        seflAssertedName = SelfAssertedName(
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
        institutionAccounts = institutionAccounts,
        dateCreated = dateCreated,
    )
}