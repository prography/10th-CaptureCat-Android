package com.prography.home

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.GetRandomImageUseCase
import com.prography.data.remote.entity.PhotoResponse
import com.prography.domain.model.PhotoModel
import com.prography.ui.BaseComposeViewModel
import com.prography.util.ext.parseErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val getRandomImageUseCase: GetRandomImageUseCase
) : BaseComposeViewModel() {

    private val _photos = MutableStateFlow<List<PhotoModel>>(emptyList())
    val photos: StateFlow<List<PhotoModel>> = _photos

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        // 글로벌 로딩 및 토스트 사용
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            getRandomImageUseCase("EXnDxBD80YuFFaRtvjYtnZFlpdFtWVLMpjlefrM86lk", 5)
                .onSuccess { result ->
                    _photos.value = result.photoList
                    delay(1000)
                    hideLoading()
                    Timber.d("photos.value = ${photos.value}")
                    showToast("성공입니다!!")
                }
                .onFailure { error ->
                    hideLoading()
                    showToast(error.message.toString())
                }
        }
    }
}
