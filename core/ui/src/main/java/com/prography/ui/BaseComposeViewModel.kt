package com.prography.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.ui.common.GlobalUiManager
import com.prography.ui.common.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseComposeViewModel<UIState, UIEffect, UIAction>(
    initialState: UIState
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<UIState> = _uiState

    val currentState: UIState
        get() = _uiState.value

    private val _effect = MutableSharedFlow<UIEffect>()
    val effect: SharedFlow<UIEffect> = _effect

    fun sendAction(action: UIAction) = handleAction(action)

    protected fun updateState(reducer: UIState.() -> UIState) {
        _uiState.value = _uiState.value.reducer()
    }

    protected fun emitEffect(effect: UIEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    protected fun showToast(message: String) {
        GlobalUiManager.sendEvent(UiEvent.ShowToast(message))
    }

    protected fun showLoading() {
        GlobalUiManager.sendEvent(UiEvent.ShowLoading)
    }

    protected fun hideLoading() {
        GlobalUiManager.sendEvent(UiEvent.HideLoading)
    }

    abstract fun handleAction(action: UIAction)
}
