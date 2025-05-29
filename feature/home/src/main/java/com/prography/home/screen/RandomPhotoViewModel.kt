package com.prography.home.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.prography.data.api.BookmarkPhotoDao
import com.prography.data.entity.BookmarkPhoto
import com.prography.data.entity.ImageUrls
import com.prography.data.entity.PhotoResponse
import com.prography.domain.model.PhotoModel
import com.prography.domain.model.UiPhotoModel
import com.prography.domain.usecase.GetRandomImageUseCase
import com.prography.ui.BaseComposeViewModel
import com.prography.util.ext.parseErrorMsg
import kotlinx.coroutines.delay

@HiltViewModel
class RandomPhotoViewModel @Inject constructor(
    private val bookmarkPhotoDao: BookmarkPhotoDao,
    private val getRandomImageUseCase: GetRandomImageUseCase
): BaseComposeViewModel() {

    private val _photos = MutableStateFlow<UiPhotoModel>(
        UiPhotoModel(photoList = emptyList())
    )
    val photos: StateFlow<UiPhotoModel> = _photos

    private val _bookmarkedPhotos = MutableStateFlow<List<BookmarkPhoto>>(emptyList())
    val bookmarkedPhotos = _bookmarkedPhotos.asStateFlow()

    init {
        fetchPhotos()
    }

    fun bookmarkPhoto(photo: PhotoModel) {
        viewModelScope.launch {
            bookmarkPhotoDao.insertBookmark(BookmarkPhoto(photo.id, ImageUrls(photo.imageUrls.small, photo.imageUrls.regular)))
        }
    }

    fun fetchBookmarks() {
        viewModelScope.launch {
            bookmarkPhotoDao.getAllBookmarks().collect { bookmarks ->
                _bookmarkedPhotos.value = bookmarks
            }
        }
    }

    fun fetchPhotos() {
        Timber.i("checking fetchPhotos")
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            getRandomImageUseCase("EXnDxBD80YuFFaRtvjYtnZFlpdFtWVLMpjlefrM86lk", 5).onSuccess {
                hideLoading()
                showToast("성공!")
                _photos.emit(it)

            }.onFailure {
                hideLoading()
                showToast(it.message.parseErrorMsg())
            }
        }
    }
}