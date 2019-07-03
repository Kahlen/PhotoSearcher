package com.photosearcher.network.download

import com.photosearcher.network.search.data.PhotoMetadata
import com.photosearcher.network.search.data.url
import com.photosearcher.util.BitmapUtils
import com.photosearcher.util.LooperProvider

class PhotoTaskCreator constructor(
    private val looperProvider: LooperProvider,
    private val bitmapUtils: BitmapUtils) {

    fun create(metadata: PhotoMetadata): PhotoTask {
        return PhotoTask(metadata.url(), looperProvider, bitmapUtils)
    }
}