package com.prography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.photo.GetAllBookmarksUseCase
import com.prography.domain.usecase.user.GetOnboardingShownUseCase
import com.prography.domain.usecase.user.SetOnboardingShownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingShown: GetOnboardingShownUseCase,
    private val setOnboardingShown: SetOnboardingShownUseCase,
    private val getAllBookmarksUseCase: GetAllBookmarksUseCase
) : ViewModel() {

    fun initChecking(){
        viewModelScope.launch {
            getAllBookmarksUseCase().collect { shown ->
                Timber.d("Shown: $shown")
            }
        }
    }
}
