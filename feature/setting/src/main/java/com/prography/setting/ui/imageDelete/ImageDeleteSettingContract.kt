package com.prography.setting.ui.imageDelete

// package com.prography.setting.ui.imageDelete

data class ImageDeleteSettingState(
    val isEnabled: Boolean = false,
    val isLoading: Boolean = false
)

sealed class ImageDeleteSettingAction {
    object Load : ImageDeleteSettingAction()
    data class Toggle(val enabled: Boolean) : ImageDeleteSettingAction()
    object ClickBack : ImageDeleteSettingAction()
}

sealed class ImageDeleteSettingEffect {
    object NavigateUp : ImageDeleteSettingEffect()
    data class ShowToast(val message: String) : ImageDeleteSettingEffect()
}
