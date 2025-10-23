package com.prography.setting.ui.withdraw

data class WithdrawState(
    val selectedReason: String = "",
    val showWithdrawDialog: Boolean = false
)

sealed class WithdrawAction {
    data class SelectReason(val reason: String): WithdrawAction()
    object ClickContinue : WithdrawAction()
    object ConfirmWithdraw : WithdrawAction()
    object Cancel : WithdrawAction()
}

sealed class WithdrawEffect {
    object NavigateToLogin : WithdrawEffect()
    object NavigateUp : WithdrawEffect()
}
