package com.prography.home.ui.mypage.ui.withdraw

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.auth.WithdrawUseCase
import com.prography.domain.usecase.screenshot.DeleteAllScreenshotsUseCase
import com.prography.domain.usecase.user.SetOnboardingShownUseCase
import com.prography.domain.repository.UserPreferenceRepository
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.user.SetStartTagScreenShownUseCase
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase,
    private val deleteAllScreenshotsUseCase: DeleteAllScreenshotsUseCase,
    private val setStartTagScreenShownUseCase: SetStartTagScreenShownUseCase,
    private val userPreferenceRepository: UserPreferenceRepository
) : BaseComposeViewModel<WithdrawState, WithdrawEffect, WithdrawAction>(
    initialState = WithdrawState()
) {
    override fun handleAction(action: WithdrawAction) {
        when(action) {
            is WithdrawAction.SelectReason -> updateState { copy(selectedReason = action.reason) }
            WithdrawAction.ClickContinue -> {
                val selectedReason = uiState.value.selectedReason
                if (selectedReason != null) {
                    viewModelScope.launch {
                        withdrawUseCase(selectedReason).onSuccess {
                            Timber.d("회원탈퇴 성공 - 데이터 초기화 시작")

                            clearUserData()
                            updateState { copy(showWithdrawDialog = true) }
                        }.onFailure {
                            showToast("회원탈퇴에 실패했습니다.")
                        }
                    }
                } else {
                    showToast("탈퇴 이유를 선택해주세요.")
                }
            }
            WithdrawAction.ConfirmWithdraw -> {
                updateState { copy(showWithdrawDialog = false) }
                emitEffect(WithdrawEffect.NavigateToLogin)
            }
            WithdrawAction.Cancel -> emitEffect(WithdrawEffect.NavigateUp)
        }
    }

    private suspend fun clearUserData() {
        Result.runCatching { deleteAllScreenshotsUseCase() }
            .onSuccess { Timber.d("로컬 DB 데이터 삭제 완료") }
            .onFailure { Timber.e(it, "로컬 DB 데이터 삭제 실패") }

        Result.runCatching { setStartTagScreenShownUseCase(false) }
            .onSuccess { Timber.d("시작태그 상태 초기화 완료") }
            .onFailure { Timber.e(it, "시작태그 상태 초기화 실패") }

        Result.runCatching { userPreferenceRepository.clearTokens() }
            .onSuccess { Timber.d("토큰 정보 삭제 완료") }
            .onFailure { Timber.e(it, "토큰 정보 삭제 실패") }
    }
}
