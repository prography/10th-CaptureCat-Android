package com.prography.auth.route.screen.contract

data class LoginState(
    val pendingAuth: PendingAuth? = null,
    val pendingLink: PendingLink? = null
) {
    val isLinkDialogVisible get() = pendingLink != null
}

data class PendingAuth(val provider: String, val idToken: String, val accessToken: String?)
data class PendingLink(val existingProvider: String?, val linkToken: String?)