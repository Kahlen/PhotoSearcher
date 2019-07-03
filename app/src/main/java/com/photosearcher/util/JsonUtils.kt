package com.photosearcher.util

import com.photosearcher.network.search.data.PhotoSearchResponseData
import com.photosearcher.network.search.data.toPhotoSearchResponseData
import org.json.JSONObject

class JsonUtils {
    fun toPhotoSearchResponseData(response: String, searchText: String): PhotoSearchResponseData? =
        JSONObject(response).toPhotoSearchResponseData(searchText)
}