package com.photosearcher.network.search

import com.photosearcher.network.search.data.PhotoSearchResponseData

/**
 * A callback to notify the search result.
 */
interface PhotoSearchListener {
    fun onSearchSucceeded(data: PhotoSearchResponseData)
    fun onSearchFailed(searchText: String)
}