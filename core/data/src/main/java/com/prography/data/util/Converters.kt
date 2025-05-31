package com.prography.data.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prography.data.remote.entity.ImageUrls

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromImageUrls(imageUrls: ImageUrls): String {
        return gson.toJson(imageUrls)
    }

    @TypeConverter
    fun toImageUrls(imageUrlsString: String): ImageUrls {
        val type = object : TypeToken<ImageUrls>() {}.type
        return gson.fromJson(imageUrlsString, type)
    }
}
