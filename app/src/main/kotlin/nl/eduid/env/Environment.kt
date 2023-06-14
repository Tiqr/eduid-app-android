package nl.eduid.env

import androidx.annotation.RawRes
import nl.eduid.R

//test2, test, acc and production
sealed class Environment(val baseUrl: String, @RawRes val authConfig: Int, val name: String) {
    object Test2 : Environment(
        baseUrl = TEST2_BASE_URL, authConfig = R.raw.auth_config_test2,
        name = "TEST2"
    )

    object Test : Environment(
        baseUrl = TEST_BASE_URL, authConfig = R.raw.auth_config_test,
        name = "TEST"
    )

    object Acceptance : Environment(
        baseUrl = ACCEPTANCE_BASE_URL,
        authConfig = R.raw.auth_config_acceptance,
        name = "ACCEPTANCE"
    )

    object Production : Environment(
        baseUrl = PRODUCTION_BASE_URL,
        authConfig = R.raw.auth_config,
        name = "PRODUCTION"
    )
}

const val TEST2_BASE_URL = "https://login.test2.eduid.nl"
const val TEST_BASE_URL = "https://login.test.eduid.nl"
const val ACCEPTANCE_BASE_URL = "https://login.acc.eduid.nl"
const val PRODUCTION_BASE_URL = "https://login.eduid.nl"
