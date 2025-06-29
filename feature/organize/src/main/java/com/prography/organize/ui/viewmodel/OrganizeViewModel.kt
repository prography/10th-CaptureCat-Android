package com.prography.organize.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.prography.organize.ui.components.TagItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OrganizeViewModel @Inject constructor() : ViewModel() {

    private val _tagItems = MutableStateFlow<List<TagItem>>(emptyList())
    val tagItems: StateFlow<List<TagItem>> = _tagItems.asStateFlow()

    init {
        // 초기 태그 세팅 (서버 또는 로컬 DB에서 불러오는 식으로 확장 가능)
        _tagItems.value = listOf(
            TagItem("1", "소원", true),
            TagItem("2", "쇼핑", false),
            TagItem("3", "음식", true)
        )
    }

    fun toggleTag(tagId: String) {
        _tagItems.update { tags ->
            val selectedCount = tags.count { it.isSelected }
            tags.map {
                if (it.id == tagId) {
                    // 최대 4개 제한
                    if (!it.isSelected && selectedCount >= 4) return@map it
                    it.copy(isSelected = !it.isSelected)
                } else it
            }
        }
    }

    fun addTag(newText: String) {
        val newId = (_tagItems.value.size + 1).toString()
        _tagItems.update { it + TagItem(id = newId, text = newText, isSelected = false) }
    }
}
