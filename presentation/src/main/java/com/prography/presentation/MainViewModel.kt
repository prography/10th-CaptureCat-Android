package com.prography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.auth.CheckLoginStatusUseCase
import com.prography.domain.usecase.auth.ObserveAuthEventsUseCase
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.domain.usecase.user.GetOnboardingShownUseCase
import com.prography.domain.usecase.user.GetStartTagScreenShownUseCase
import com.prography.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingShownUseCase: GetOnboardingShownUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val getStartTagScreenShownUseCase: GetStartTagScreenShownUseCase,
    private val observeAuthEventsUseCase: ObserveAuthEventsUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<AppRoute?>(null)
    val startDestination = _startDestination.asStateFlow()

    val isReady = MutableStateFlow(false)

    private val _shouldNavigateToLogin = MutableStateFlow(false)
    val shouldNavigateToLogin = _shouldNavigateToLogin.asStateFlow()

    init {
        // AuthRepository의 이벤트 구독
        viewModelScope.launch {
            observeAuthEventsUseCase().collect { event ->
                when (event) {
                    is com.prography.domain.repository.AuthRepository.AuthEvent.RefreshTokenExpired -> {
                        Timber.d("Refresh token expired, should navigate to login")
                        _shouldNavigateToLogin.value = true
                    }
                }
            }
        }
    }

    fun initChecking() {
        viewModelScope.launch {
            val isOnboardingShown = getOnboardingShownUseCase().first()
            val isLoggedIn = checkLoginStatusUseCase()
            val isStartTagScreenShown = getStartTagScreenShownUseCase().first()

            _startDestination.value  = AppRoute.Main()

/*            _startDestination.value  = when {
                !isOnboardingShown -> AppRoute.InitOnboarding // 온보딩을 보지 않은 경우, 초기 온보딩 화면
                isLoggedIn -> AppRoute.Main() // 로그인 유저일 경우, 메인 화면
                !isStartTagScreenShown -> AppRoute.Login // 게스트 모드일 경우, 시작하기를 안봤다면 로그인 화면
                else -> AppRoute.Main()  // 시작하기를 봤다면 메인 화면
            }*/

            isReady.value = true
        }
    }

    fun onNavigatedToLogin() {
        _shouldNavigateToLogin.value = false
    }
}
