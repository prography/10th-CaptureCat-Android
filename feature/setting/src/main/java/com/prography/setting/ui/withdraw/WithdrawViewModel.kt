package com.prography.setting.ui.withdraw

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.auth.WithdrawUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase
) : BaseComposeViewModel<WithdrawState, WithdrawEffect, WithdrawAction>(
    initialState = WithdrawState()
) {
    override fun handleAction(action: WithdrawAction) {
        when(action) {
            is WithdrawAction.SelectReason -> updateState { copy(selectedReason = action.reason) }
            WithdrawAction.ClickContinue -> {
                viewModelScope.launch {
                    withdrawUseCase().onSuccess {
                        updateState { copy(showWithdrawDialog = true) }
                    }.onFailure {
                        showToast("회원탈퇴에 실패했습니다.")
                    }
                }
            }
            WithdrawAction.ConfirmWithdraw -> {
                updateState { copy(showWithdrawDialog = false) }
                emitEffect(WithdrawEffect.NavigateToLogin)
            }
            WithdrawAction.Cancel -> emitEffect(WithdrawEffect.NavigateUp)
        }
    }
}
