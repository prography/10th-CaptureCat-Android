package com.prography.ui

import androidx.lifecycle.ViewModel
import com.prography.ui.common.GlobalUiManager
import com.prography.ui.common.UiEvent

// Base ViewModel 수정
abstract class BaseComposeViewModel : ViewModel() {
    fun showLoading() {
        GlobalUiManager.sendEvent(UiEvent.ShowLoading)
    }

    fun hideLoading() {
        GlobalUiManager.sendEvent(UiEvent.HideLoading)
    }

    fun showToast(message: String) {
        GlobalUiManager.sendEvent(UiEvent.ShowToast(message))
    }
}