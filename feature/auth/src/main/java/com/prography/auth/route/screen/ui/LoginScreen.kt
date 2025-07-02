package com.prography.auth.route.screen.ui

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
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
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigationHelper: NavigationHelper
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
                        onSuccess = { token ->
                            Timber.d("Kakao login success. Token: $token")
                            navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
                        },
                        onFailure = { error ->
                            Timber.e("Kakao login failed: $error")
                        }
                    )
                }
                LoginEffect.StartGoogleLogin -> {
                    handleGoogleLogin(
                        context = context,
                        onSuccess = { user ->
                            Timber.d("Login success: ${user.email}")
                            navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
                        },
                        onFailure = { error ->
                            Timber.e("Login failed: $error")
                        }
                    )
                }
                LoginEffect.NavigateToHome -> {
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Start, popUpTo = true))
                    // navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
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
    onSuccess: (String) -> Unit,
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
                token != null -> {
                    onSuccess(token.accessToken)
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
                } else if (token != null) {
                    onSuccess(token.accessToken)
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
    onSuccess: (FirebaseUser) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    try {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .build()

        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val idTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = idTokenCredential.idToken

            if (idToken.isNullOrBlank()) {
                throw IllegalStateException("ID Token is null or blank")
            }

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                .addOnSuccessListener { onSuccess(it.user!!) }
                .addOnFailureListener { onFailure(it) }
        } else {
            throw IllegalStateException("Invalid Credential type: ${credential::class.simpleName}")
        }
    } catch (e: Exception) {
        onFailure(e)
    }
}
