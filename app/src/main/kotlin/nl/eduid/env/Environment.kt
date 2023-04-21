package nl.eduid.env

import androidx.annotation.RawRes
import nl.eduid.R

//test2, test, acc and production
sealed class Environment(val baseUrl: String, @RawRes val authConfig: Int) {
    object Test2 : Environment(TEST2_BASE_URL, R.raw.auth_config_test2)
    object Test : Environment(TEST_BASE_URL, R.raw.auth_config_test)
    object Acceptance : Environment(ACCEPTANCE_BASE_URL, R.raw.auth_config_acceptance)
    object Production : Environment(PRODUCTION_BASE_URL, R.raw.auth_config)
}

const val TEST2_BASE_URL = "https://login.test2.eduid.nl"
const val TEST_BASE_URL = "https://login.test.eduid.nl"
const val ACCEPTANCE_BASE_URL = "https://login.acc.eduid.nl"
const val PRODUCTION_BASE_URL = "https://login.eduid.nl"
