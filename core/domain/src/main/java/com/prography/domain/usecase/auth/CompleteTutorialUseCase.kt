package com.prography.domain.usecase.auth

import com.prography.domain.repository.AuthRepository
import com.prography.domain.usecase.user.SetStartTagScreenShownUseCase
import javax.inject.Inject

class CompleteTutorialUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val setStartTagScreenShownUseCase: SetStartTagScreenShownUseCase
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // 로컬 상태 업데이트 (항상 실행)
            setStartTagScreenShownUseCase(true)

            // 로그인 상태 확인
            if (checkLoginStatusUseCase()) {
                // 로그인한 유저: 서버 API도 호출
                authRepository.completeTutorial()
            } else {
                // 게스트 모드: 로컬 업데이트만 완료
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}