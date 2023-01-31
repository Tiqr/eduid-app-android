package nl.eduid.di.model


data class RequestNewIdRequest(
    val user: User,
    val authenticationRequestId: String,
) {
    data class User(
        val email: String,
        val givenName: String,
        val familyName: String,
    )
}

//{"user":{"email":"test4@test.com","givenName":"Tester","familyName":"Testerson"},"authenticationRequestId":"48e0eb5f-62ae-429e-b103-444ad24f2cc0"}