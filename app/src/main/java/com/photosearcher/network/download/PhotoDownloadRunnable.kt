package com.photosearcher.network.download

import android.graphics.BitmapFactory
import com.photosearcher.util.BitmapUtils
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * A runnable to download a photo.
 */
class PhotoDownloadRunnable constructor(
    private val url: URL,
    private val bitmapUtils: BitmapUtils,
    private val listener: PhotoDownloadStateChangeListener
): Runnable {

    private val thread = Thread.currentThread()

    override fun run() {
        listener.onPhotoDownloadStateChanged(PhotoDownloadState.DOWNLOADING)

        var inputStream: InputStream? = null
        try {
            val httpConnection = url.openConnection() as HttpURLConnection
            inputStream = httpConnection.inputStream
            if (!thread.isInterrupted) {
                listener.onPhotoDownloadStateChanged(PhotoDownloadState.COMPLETED, bitmapUtils.toBitmap(inputStream))
            }
        } catch (exception: Exception) {
            if (exception !is InterruptedIOException) {
                listener.onPhotoDownloadStateChanged(PhotoDownloadState.FAILED)
            }
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {

            }
        }
    }

    fun cancelDownload() {
        thread.interrupt()
    }
}