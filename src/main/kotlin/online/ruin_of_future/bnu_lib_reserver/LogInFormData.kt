package online.ruin_of_future.bnu_lib_reserver

class LogInFormData(
    SYNCHRONIZER_TOKEN: String,
    SYNCHRONIZER_URI: String,
    username: String,
    password: String,
    authid: String
) {
    val SYNCHRONIZER_TOKEN = LogicalBridge.escape(SYNCHRONIZER_TOKEN)
    val SYNCHRONIZER_URI = LogicalBridge.escape(SYNCHRONIZER_URI)
    val username = LogicalBridge.escape(username)
    val password = LogicalBridge.escape(password)
    val authid = LogicalBridge.escape(authid)
}