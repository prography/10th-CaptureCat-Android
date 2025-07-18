import com.prography.domain.model.TagModel

sealed class ImageDetailAction {
    // ... other actions ...
    data class OnTagDelete(val tag: TagModel) : ImageDetailAction()
    // ... other actions ...
}