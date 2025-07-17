package com.android.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.tag.AddRecentTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StartTagViewModel @Inject constructor(
    private val addRecentTagsUseCase: AddRecentTagsUseCase
) : ViewModel() {

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