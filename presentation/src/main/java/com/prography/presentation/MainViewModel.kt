package com.prography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.repository.UserPreferenceRepository
import com.prography.domain.usecase.auth.GetAuthTokenUseCase
import com.prography.domain.usecase.user.GetOnboardingShownUseCase
import com.prography.domain.usecase.user.SetOnboardingShownUseCase
import com.prography.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingShownUseCase: GetOnboardingShownUseCase,
    private val getAuthTokenUseCase: GetAuthTokenUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<AppRoute?>(null)
    val startDestination = _startDestination.asStateFlow()

    val isReady = MutableStateFlow(false)

    fun initChecking() {
        viewModelScope.launch {
            val isOnboardingShown = getOnboardingShownUseCase.invoke().first()
            val isLoggedIn = getAuthTokenUseCase.isLoggedIn()

            _startDestination.value  = when {
                !isOnboardingShown -> AppRoute.Onboarding // 처음 앱에 진입하는 경우, 온보딩
                !isLoggedIn -> AppRoute.Login // 토큰이 존재하지 않는 경우, 메인 화면
                else -> AppRoute.Main // 토큰이 존재하는 경우, 메인 화면 : 유효 검사 interceptor 추후 처리
            }

            isReady.value = true
        }
    }
}
