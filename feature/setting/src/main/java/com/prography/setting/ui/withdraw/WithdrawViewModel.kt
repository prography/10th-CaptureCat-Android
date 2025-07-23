package com.prography.setting.ui.withdraw

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.prography.ui.BaseComposeViewModel

@HiltViewModel
class WithdrawViewModel @Inject constructor() : BaseComposeViewModel<WithdrawState, WithdrawEffect, WithdrawAction>(
    initialState = WithdrawState()
) {
    override fun handleAction(action: WithdrawAction) {
        when(action) {
            is WithdrawAction.SelectReason -> updateState { copy(selectedReason = action.reason) }
            WithdrawAction.ClickContinue -> updateState { copy(showWithdrawDialog = true) }
            WithdrawAction.ConfirmWithdraw -> {
                updateState { copy(showWithdrawDialog = false) }
                emitEffect(WithdrawEffect.NavigateToLogin)
            }
            WithdrawAction.Cancel -> emitEffect(WithdrawEffect.NavigateUp)
        }
    }
}
