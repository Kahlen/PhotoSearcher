package com.photosearcher.network.search

import com.photosearcher.util.JsonUtils
import java.net.URL
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * A singleton class to search photos.
 */
class PhotoSearchApi private constructor(private val jsonUtils: JsonUtils) {

    private val executor = ThreadPoolExecutor(
        1, 1, 1, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>())
    private var future: Future<*>? = null

    fun search(url: URL, text: String, listener: PhotoSearchListener) {
        future = executor.submit(PhotoSearchRunnable(url, text, listener, jsonUtils))
    }

    fun cancelSearch() {
        future?.cancel(true)
    }

    companion object {
        @Volatile
        private var instance: PhotoSearchApi? = null

        fun getInstance(jsonUtils: JsonUtils): PhotoSearchApi {
            return instance ?: synchronized(this) {
                instance ?: PhotoSearchApi(jsonUtils).also { instance = it }
            }
        }
    }
}