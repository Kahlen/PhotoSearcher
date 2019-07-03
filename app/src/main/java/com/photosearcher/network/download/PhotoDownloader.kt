package com.photosearcher.network.download

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * A singleton class to download photos.
 */
class PhotoDownloader private constructor() {

    private val executor = ThreadPoolExecutor(
        8,
        8,
        1L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>())

    fun download(task: PhotoTask) {
        executor.execute(task.runnable)
    }

    companion object {
        val instance = PhotoDownloader()
    }
}