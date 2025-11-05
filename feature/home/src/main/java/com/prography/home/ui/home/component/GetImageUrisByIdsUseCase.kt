package com.prography.home.ui.home.component

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetImageUrisByIdsUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cr get() = context.contentResolver
    private val projection = arrayOf(BaseColumns._ID)

    operator fun invoke(ids: List<String>): List<Uri> =
        ids.mapNotNull { it.toLongOrNull() }
            .mapNotNull { id ->
                // 기본 external
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                if (exists(uri)) return@mapNotNull uri

                // Q+ (optional) external_primary
                val q = runCatching {
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                        id
                    )
                }.getOrNull()
                if (q != null && exists(q)) q else null
            }

    private fun exists(uri: Uri): Boolean =
        cr.query(uri, projection, null, null, null)?.use { it.moveToFirst() } == true
}
