package com.prography.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromImageUrls(imageUrls: List<String>): String {
        return gson.toJson(imageUrls)
    }

    @TypeConverter
    fun toImageUrls(imageUrlsString: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(imageUrlsString, type)
    }
}