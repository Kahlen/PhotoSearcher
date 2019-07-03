package com.photosearcher.network.search.data

import org.json.JSONObject

/**
 * The response data of search API.
 */
data class PhotoSearchResponseData(
    val page: Int,
    val totalPages: Int,
    val photoMetadata: List<PhotoMetadata>,
    val searchText: String
)

fun JSONObject.toPhotoSearchResponseData(text: String): PhotoSearchResponseData? {
    val raw = getJSONObject("photos")
    val page = raw.getInt("page")
    val totalPages = raw.getInt("pages")
    val photos = raw.getJSONArray("photo")
    val metadata = mutableListOf<PhotoMetadata>()
    for (i in 0 until photos.length()) {
        val photo = photos.getJSONObject(i)
        metadata.add(PhotoMetadata(
            farm = photo.getInt("farm"),
            server = photo.getString("server"),
            id = photo.getString("id"),
            secret = photo.getString("secret"),
            title = photo.getString("title")))
    }
    return PhotoSearchResponseData(page, totalPages, metadata, text)
}

fun PhotoSearchResponseData.isLastPage() = page == totalPages