package com.photosearcher.network.download

/**
 * Types of photo download states.
 */
enum class PhotoDownloadState(val value: Int) {
    DOWNLOADING(1),
    COMPLETED(2),
    FAILED(3)
}