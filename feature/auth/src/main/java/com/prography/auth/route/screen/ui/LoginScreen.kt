package com.prography.auth.route.screen.ui

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.prography.auth.BuildConfig
import com.prography.auth.route.screen.contract.LoginEffect
import com.prography.auth.route.screen.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                LoginEffect.StartKakaoLogin -> {
                    handleKakaoLogin(
                        context = context,
                        onSuccess = { idToken, accessToken ->
                            Timber.d("Kakao login success. idToken $idToken accessToken: $accessToken")
                            viewModel.handleKakaoLoginSuccess(idToken, accessToken)
                        },
                        onFailure = { error ->
                            Timber.e("Kakao login failed: $error")
                        }
                    )
                }
                LoginEffect.StartGoogleLogin -> {
                    handleGoogleLogin(
                        context = context,
                        onSuccess = { idToken, userId ->
                            viewModel.handleGoogleLoginSuccess(idToken, userId)
                        },
                        onFailure = { error ->
                            Timber.e("Login failed: $error")
                        }
                    )
                }
            }
        }
    }

    LoginContent(
        state = state,
        onAction = { viewModel.sendAction(it) }
    )
}

suspend fun handleKakaoLogin(
    context: Context,
    onSuccess: (String, String) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    try {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            when {
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    onFailure(IllegalStateException("User cancelled Kakao login dialog"))
                }
                error != null -> {
                    onFailure(error)
                }
                token?.idToken != null  -> {
                    onSuccess(token.idToken!!, token.accessToken)
                }
                else -> {
                    onFailure(IllegalStateException("Kakao login failed: Token is null"))
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        onFailure(IllegalStateException("User cancelled Kakao login dialog"))
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(context = context, callback =callback)
                    }
                } else if (token?.idToken != null) {
                    onSuccess(token.idToken!!, token.accessToken)
                } else {
                    onFailure(IllegalStateException("Kakao login failed without error or token"))
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context = context, callback = callback)
        }
    } catch (e: Throwable) {
        onFailure(e)
    }
}

suspend fun handleGoogleLogin(
    context: Context,
    onSuccess: (String, String) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    try {
        val (idToken, userId) = getGoogleIdTokenAndUserId(context)
        onSuccess(idToken, userId)
    } catch (e: Exception) {
        onFailure(e)
    }
}

private suspend fun getGoogleIdTokenAndUserId(context: Context): Pair<String, String> {
    val credentialManager = CredentialManager.create(context)

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(buildGoogleIdOption())
        .build()

    val result = credentialManager.getCredential(context, request)
    val credential = result.credential

    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken =
            requireNotNull(googleCredential.idToken) { "Google ID Token is null or blank." }
        val userId = requireNotNull(googleCredential.id) { "Google User ID is null or blank." }
        return idToken to userId
    } else {
        throw IllegalStateException("Expected GoogleIdTokenCredential but was ${credential::class.simpleName}")
    }
}

private fun buildGoogleIdOption(): GetGoogleIdOption {
    return GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .setAutoSelectEnabled(false)
        .build()
}
