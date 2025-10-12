package com.prography.setting.ui.imageDelete

// package com.prography.setting.ui.imageDelete

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.user.GetDeletePromptSettingUseCase
import com.prography.domain.usecase.user.SetDeletePromptSettingUseCase
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber


@HiltViewModel
class ImageDeleteSettingViewModel @Inject constructor(
    private val getDeletePromptSettingUseCase: GetDeletePromptSettingUseCase,
    private val setDeletePromptSettingUseCase: SetDeletePromptSettingUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<ImageDeleteSettingState, ImageDeleteSettingEffect, ImageDeleteSettingAction>(
    initialState = ImageDeleteSettingState()
) {

    override fun handleAction(action: ImageDeleteSettingAction) {
        when (action) {
            ImageDeleteSettingAction.Load -> load()
            is ImageDeleteSettingAction.Toggle -> toggle(action.enabled)
            ImageDeleteSettingAction.ClickBack -> navigationHelper.navigate(NavigationEvent.Up)
        }
    }

    private fun load() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            getDeletePromptSettingUseCase()
                .onSuccess { enabled ->
                    updateState { copy(isEnabled = enabled, isLoading = false) }
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to load delete prompt setting")
                    updateState { copy(isLoading = false) }
                    emitEffect(ImageDeleteSettingEffect.ShowToast("설정을 불러오지 못했어요."))
                }
        }
    }

    private fun toggle(enabled: Boolean) {
        val prev = currentState.isEnabled
        updateState { copy(isEnabled = enabled) }

        viewModelScope.launch {
            setDeletePromptSettingUseCase(enabled)
                .onSuccess {
                    Timber.d("Delete prompt setting updated: $enabled")
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to update delete prompt setting")
                    // 롤백
                    updateState { copy(isEnabled = prev) }
                    emitEffect(ImageDeleteSettingEffect.ShowToast("설정 저장에 실패했어요."))
                }
        }
    }
}
