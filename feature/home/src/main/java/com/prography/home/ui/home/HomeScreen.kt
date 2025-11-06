package com.prography.home.ui.home

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeEffect
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import kotlinx.coroutines.flow.collectLatest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.compose.collectAsLazyPagingItems
import com.prography.ui.component.UiCommonDialog
import androidx.core.net.toUri
import com.prography.home.ui.home.component.DeleteChoiceBottomSheet
import com.prography.util.MixpanelUtil

@Composable
fun HomeScreen(
    screenshotIds: List<String>,
    onNavigateToStorage: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val context = LocalContext.current

    val pagingItems = viewModel.screenshotsPagingFlow.collectAsLazyPagingItems()

    // 처음 접근 시 로그인 상태 체크 및 인기 태그 로드
    LaunchedEffect(Unit) {
        MixpanelUtil.track("view_home")
        viewModel.checkLoginStatusOnFirstAccess()
        viewModel.loadMostUsedTags()
    }

    LaunchedEffect(screenshotIds) {
        if (screenshotIds.isNotEmpty()) {
            viewModel.sendAction(HomeAction.OnArriveWithIds(screenshotIds))
        }
    }


    // 시스템 삭제 런처
    var pendingUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // 성공 개수는 OS에서 직접 주지 않으니 "시도 개수"로 보고
        val successCount = if (result.resultCode == Activity.RESULT_OK) pendingUris.size else 0
        viewModel.sendAction(HomeAction.OnSystemDeleteResult(successCount))
        pendingUris = emptyList()
    }

    // 바텀싯 표시 여부
    var showDeleteSheet by remember { mutableStateOf(false) }
    var idsForSheet by remember { mutableStateOf<List<String>>(emptyList()) }


    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is HomeEffect.ShowError -> {
                    println("Home Error: ${effect.message}")
                }
                is HomeEffect.NavigateToStorage -> {
                    onNavigateToStorage()
                }
                is HomeEffect.OpenExternalLink -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {

                    }
                }
                is HomeEffect.ShowDeleteChoiceBottomSheet -> {
                    idsForSheet = effect.ids
                    showDeleteSheet = true
                }

                is HomeEffect.RequestSystemDelete -> {
                    val uris = effect.uris
                    if (uris.isEmpty()) return@collectLatest
                    pendingUris = uris

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        runCatching {
                            val req = MediaStore.createDeleteRequest(
                                context.contentResolver, uris
                            )
                            deleteLauncher.launch(
                                IntentSenderRequest.Builder(req.intentSender).build()
                            )
                        }.onFailure {
                            // 실패 시 결과 0으로 보고
                            viewModel.sendAction(HomeAction.OnSystemDeleteResult(0))
                            pendingUris = emptyList()
                        }
                    } else {
                        var success = 0
                        uris.forEach { u ->
                            if (runCatching { context.contentResolver.delete(u, null, null) }.isSuccess) {
                                success++
                            }
                        }
                        viewModel.sendAction(HomeAction.OnSystemDeleteResult(success))
                        pendingUris = emptyList()
                    }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        HomeContent(
            state = state,
            onAction = { action -> viewModel.sendAction(action) },
            pagingItems = pagingItems
        )
        CaptureCatFab(
            onUploadClick = { viewModel.sendAction(HomeAction.NavigateToStorageUpload) },
            onOrganizeClick = { viewModel.sendAction(HomeAction.NavigateToStorageOrganize) }
        )
    }

    if (state.showLoginDialog) {
        UiCommonDialog(
            isVisible = true,
            title = stringResource(com.prography.ui.R.string.login_title),
            message = stringResource(com.prography.ui.R.string.login_message),
            leftButtonText = stringResource(com.prography.ui.R.string.common_cancel),
            rightButtonText = stringResource(com.prography.ui.R.string.common_confirm),
            onDismiss = { viewModel.sendAction(HomeAction.HideLoginDialog) },
            onConfirm = { viewModel.sendAction(HomeAction.NavigateToLogin) }
        )
    }
    else if (showDeleteSheet) {
        DeleteChoiceBottomSheet(
            onDeleteNow = {
                showDeleteSheet = false
                viewModel.sendAction(HomeAction.OnDeleteChoiceConfirm(idsForSheet))
            },
            onDismiss = {
                showDeleteSheet = false
                viewModel.sendAction(HomeAction.OnDeleteChoiceLater)
            }
        )
    }
}


// Navigation Helper Wrapper for Hilt injection
@dagger.hilt.android.lifecycle.HiltViewModel
class NavigationHelperWrapper @javax.inject.Inject constructor(
    val navigationHelper: NavigationHelper
) : androidx.lifecycle.ViewModel()
