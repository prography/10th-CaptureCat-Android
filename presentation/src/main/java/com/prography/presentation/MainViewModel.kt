package com.prography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.photo.GetAllBookmarksUseCase
import com.prography.domain.usecase.user.GetOnboardingShownUseCase
import com.prography.domain.usecase.user.SetOnboardingShownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingShown: GetOnboardingShownUseCase,
    private val setOnboardingShown: SetOnboardingShownUseCase,
    private val getAllBookmarksUseCase: GetAllBookmarksUseCase
) : ViewModel() {

    private val _isOnboardingShown = MutableStateFlow<Boolean?>(null)
    val isOnboardingShown: StateFlow<Boolean?> = _isOnboardingShown

    fun initChecking() {
        viewModelScope.launch {
            getOnboardingShown().collect { shown ->
                _isOnboardingShown.value = shown
                Timber.d("Shown: $shown")
            }
        }
    }
}
