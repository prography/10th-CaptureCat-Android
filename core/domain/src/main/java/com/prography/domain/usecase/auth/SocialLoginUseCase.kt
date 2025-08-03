package com.prography.domain.usecase.auth

import com.prography.domain.model.LoginResult
import com.prography.domain.repository.AuthRepository
import com.prography.domain.usecase.screenshot.GetAllLocalScreenshotsUseCase
import com.prography.domain.usecase.user.GetStartTagScreenShownUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val getStartTagScreenShownUseCase: GetStartTagScreenShownUseCase,
    private val completeTutorialUseCase: CompleteTutorialUseCase,
    private val getAllLocalScreenshotsUseCase: GetAllLocalScreenshotsUseCase,
) {
    suspend operator fun invoke(
        provider: String,
        idToken: String,
        accessToken: String? = null
    ): Result<Pair<LoginNavigationResult, LoginResult>> {
        return authRepository.socialLogin(provider, idToken, accessToken).mapCatching { loginResult ->
            val hasSeenLocalStartTag = getStartTagScreenShownUseCase().first()

            val navigationResult = when {
                // 기기 내 시작하기를 완료하지 않았고, 서버에서도 튜토리얼이 완료되지 않음
                !hasSeenLocalStartTag && !loginResult.tutorialCompleted -> {
                    LoginNavigationResult.NavigateToStartTag
                }

                // 기기 내 시작하기를 완료함
                hasSeenLocalStartTag -> {
                    // 1. 서버에서 튜토리얼이 완료되지 않은 상태라면, 튜토리얼 완료 여부 서버로 값 보내기
                    if (!loginResult.tutorialCompleted)
                        completeTutorialUseCase()

                    // 2. 동기화 여부 판단
                    // 로컬 데이터가 있음 > 동기화 업로드 화면으로 이동
                    // 로컬 데이터가 없음 > 홈 화면으로 이동
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
            navigationResult to loginResult
        }
    }
}

sealed class LoginNavigationResult {
    object NavigateToStartTag : LoginNavigationResult()
    object NavigateToHome : LoginNavigationResult()
    object NavigateToUpload : LoginNavigationResult()
}