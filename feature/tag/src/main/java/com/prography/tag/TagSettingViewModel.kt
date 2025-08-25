package com.prography.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.model.TagWithCount
import com.prography.domain.usecase.screenshot.GetMostUsedTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TagSettingUiState(
    val tags: List<TagWithCount> = emptyList(),
    val tagCount: Int = 0,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTags: Set<String> = emptySet()
)

@HiltViewModel
class TagSettingViewModel @Inject constructor(
    private val getMostUsedTagsUseCase: GetMostUsedTagsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TagSettingUiState())
    val uiState: StateFlow<TagSettingUiState> = _uiState.asStateFlow()

    fun loadTags() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val tags = getMostUsedTagsUseCase(30)
                _uiState.value = _uiState.value.copy(
                    tags = tags,
                    tagCount = tags.size,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditMode = !_uiState.value.isEditMode,
            selectedTags = emptySet()
        )
    }

    fun toggleTagSelection(tag: String) {
        _uiState.value = _uiState.value.copy(
            selectedTags = _uiState.value.selectedTags.let {
                if (it.contains(tag)) it - tag else it + tag
            }
        )
    }

    fun selectAllTags() {
        _uiState.value = _uiState.value.copy(
            selectedTags = _uiState.value.tags.map { it.tag }.toSet()
        )
    }

    fun clearAllSelections() {
        _uiState.value = _uiState.value.copy(selectedTags = emptySet())
    }

    fun deleteSelectedTags() {
        viewModelScope.launch {
            try {
                val updatedTags =
                    _uiState.value.tags.filter { !_uiState.value.selectedTags.contains(it.tag) }
                _uiState.value = _uiState.value.copy(
                    tags = updatedTags,
                    tagCount = updatedTags.size,
                    selectedTags = emptySet()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteAllTags() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    tags = emptyList(),
                    tagCount = 0,
                    selectedTags = emptySet()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }
}