package com.photosearcher.network.search

import com.photosearcher.util.JsonUtils
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InterruptedIOException

/**
 * A runnable to search photos.
 */
class PhotoSearchRunnable constructor(
    private val url: URL,
    private val text: String,
    private val listener: PhotoSearchListener,
    private val jsonUtils: JsonUtils): Runnable {

    override fun run() {
        var inputStream: InputStream? = null
        try {
            val httpConnection = url.openConnection() as HttpURLConnection
            inputStream = httpConnection.inputStream
            val br = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()

            while (true) {
                val line = br.readLine() ?: break
                sb.append(line + "\n")
            }
            br.close()
            if (!Thread.currentThread().isInterrupted) {
                listener.onSearchSucceeded(jsonUtils.toPhotoSearchResponseData(sb.toString(), text)!!)
            }
        } catch (exception: Exception) {
            if (exception !is InterruptedIOException) {
                listener.onSearchFailed(text)
            }
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {

            }
        }
    }
}