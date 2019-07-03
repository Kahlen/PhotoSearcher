package com.photosearcher.network.search.data

/**
 * The photo data retrieved from searching and is used compose the url to download a photo.
 */
data class PhotoMetadata(
    val farm: Int,
    val server: String,
    val id: String,
    val secret: String,
    val title: String?
)

fun PhotoMetadata.url() = "https://farm$farm.static.flickr.com/$server/${id}_$secret.jpg"