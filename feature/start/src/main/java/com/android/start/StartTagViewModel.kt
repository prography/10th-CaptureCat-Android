package com.android.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.tag.AddRecentTagsUseCase
import com.prography.domain.usecase.user.GetStartTagScreenShownUseCase
import com.prography.domain.usecase.auth.CompleteTutorialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StartTagViewModel @Inject constructor(
    private val addRecentTagsUseCase: AddRecentTagsUseCase,
    private val getStartTagScreenShownUseCase: GetStartTagScreenShownUseCase,
    private val completeTutorialUseCase: CompleteTutorialUseCase
) : ViewModel() {

    val isStartTagScreenShown: Flow<Boolean> = getStartTagScreenShownUseCase()

    init {
        // 화면 접근 시 튜토리얼 완료 처리
        completeTutorial()
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

    fun saveSelectedTags(tags: List<String>) {
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