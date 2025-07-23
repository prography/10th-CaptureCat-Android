package com.prography.domain.usecase.auth

import com.prography.domain.model.LoginResult
import com.prography.domain.repository.AuthRepository
import com.prography.domain.usecase.screenshot.GetAllLocalScreenshotsUseCase
import com.prography.domain.usecase.user.GetStartTagScreenShownUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val getStartTagScreenShownUseCase: GetStartTagScreenShownUseCase,
    private val completeTutorialUseCase: CompleteTutorialUseCase,
    private val getAllLocalScreenshotsUseCase: GetAllLocalScreenshotsUseCase,
) {
    suspend operator fun invoke(provider: String, idToken: String): Result<LoginNavigationResult> {
        return authRepository.socialLogin(provider, idToken).mapCatching { loginResult ->
            val hasSeenLocalStartTag = getStartTagScreenShownUseCase().first()

            when {
                // 기기 내 시작하기를 완료하지 않았고, 서버에서도 튜토리얼이 완료되지 않음
                !hasSeenLocalStartTag && !loginResult.tutorialCompleted -> {
                    LoginNavigationResult.NavigateToStartTag
                }

                // 기기 내 시작하기를 완료했지만, 서버에서는 튜토리얼이 완료되지 않음
                hasSeenLocalStartTag && !loginResult.tutorialCompleted -> {
                    val localScreenshots = getAllLocalScreenshotsUseCase().first()
                    if (localScreenshots.isEmpty()) {
                        LoginNavigationResult.NavigateToHome
                    } else {
                        LoginNavigationResult.NavigateToUpload
                    }
                }

                // 그 외의 경우 (튜토리얼이 완료된 상태)
                else -> {
                    LoginNavigationResult.NavigateToHome
                }
            }
        }
    }
}

sealed class LoginNavigationResult {
    object NavigateToStartTag : LoginNavigationResult()
    object NavigateToHome : LoginNavigationResult()
    object NavigateToUpload : LoginNavigationResult()
}