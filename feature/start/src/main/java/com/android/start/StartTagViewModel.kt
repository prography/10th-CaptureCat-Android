package com.android.start

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.tag.AddRecentTagsUseCase
import com.prography.domain.usecase.user.GetStartTagScreenShownUseCase
import com.prography.domain.usecase.auth.CompleteTutorialUseCase
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// State
data class StartTagState(
    val selectedTags: List<String> = emptyList(),
    val maxSelectableTags: Int = 5
)

// Action
sealed class StartTagAction {
    data class ToggleTag(val tag: String) : StartTagAction()
    data class SaveSelectedTags(val tags: List<String>) : StartTagAction()
}

@HiltViewModel
class StartTagViewModel @Inject constructor(
    private val addRecentTagsUseCase: AddRecentTagsUseCase,
    private val completeTutorialUseCase: CompleteTutorialUseCase
) : BaseComposeViewModel<StartTagState, Nothing, StartTagAction>(
    initialState = StartTagState()
) {

    init {
        // 화면 접근 시 튜토리얼 완료 처리
        completeTutorial()
    }

    override fun handleAction(action: StartTagAction) {
        when (action) {
            is StartTagAction.ToggleTag -> toggleTag(action.tag)
            is StartTagAction.SaveSelectedTags -> saveSelectedTags(action.tags)
        }
    }

    private fun toggleTag(tag: String) {
        val currentTags = currentState.selectedTags
        val maxTags = currentState.maxSelectableTags

        Timber.d("toggleTag: $tag")
        if (tag in currentTags) {
            // 이미 선택된 태그면 제거
            updateState { copy(selectedTags = selectedTags - tag) }
        } else if (currentTags.size < maxTags) {
            // 최대 개수 미만이면 추가
            updateState { copy(selectedTags = selectedTags + tag) }
        } else {
            // 5개 초과 시 토스트 메시지
            showToast("최대 ${maxTags}개까지만 선택할 수 있습니다.")
        }
    }

    private fun completeTutorial() {
        viewModelScope.launch {
            completeTutorialUseCase()
                .onSuccess {
                    Timber.d("Tutorial completed successfully")
                }
                .onFailure { exception ->
                    Timber.e(exception, "Failed to complete tutorial")
                }
        }
    }

    private fun saveSelectedTags(tags: List<String>) {
        viewModelScope.launch {
            try {
                addRecentTagsUseCase(tags)
                Timber.d("Selected tags saved to recent tags: $tags")
            } catch (e: Exception) {
                Timber.e(e, "Failed to save selected tags")
            }
        }
    }
}