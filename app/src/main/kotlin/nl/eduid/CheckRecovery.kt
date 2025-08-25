package nl.eduid

class CheckRecovery() {

    var isQrEnrollment: Boolean = false

    fun shouldAppDoRecovery(): Boolean {
        return !isQrEnrollment
    }
}