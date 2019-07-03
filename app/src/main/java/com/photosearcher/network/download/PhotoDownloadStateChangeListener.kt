package com.photosearcher.network.download

import android.graphics.Bitmap

/**
 * Callback when photo download state changes.
 */
interface PhotoDownloadStateChangeListener {
    fun onPhotoDownloadStateChanged(state: PhotoDownloadState, bitmap: Bitmap? = null)
}