package com.photosearcher.list.viewholder

import android.graphics.Bitmap

/**
 * A callback for image download.
 */
interface PhotoDownloadCompletedListener {
    fun bitmapDownloaded(bitmap: Bitmap?)
}