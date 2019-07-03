package com.photosearcher.network.download

import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import com.photosearcher.list.viewholder.PhotoDownloadCompletedListener
import com.photosearcher.util.BitmapUtils
import com.photosearcher.util.LooperProvider
import java.net.URL
import java.util.Optional
import kotlin.properties.Delegates

/**
 * A class to handle photo download and communication with the UI.
 */
class PhotoTask constructor(
    url: String,
    looperProvider: LooperProvider,
    bitmapUtils: BitmapUtils) {

    var bitmap: Optional<Bitmap> by Delegates.observable(Optional.empty()) { _, _, new ->
        if (new.isPresent) {
            bitmapDownloadListener?.bitmapDownloaded(new.get())
        }
    }
        private set

    val runnable = PhotoDownloadRunnable(URL(url), bitmapUtils, object: PhotoDownloadStateChangeListener {
        override fun onPhotoDownloadStateChanged(state: PhotoDownloadState, bitmap: Bitmap?) {
            handler.obtainMessage(state.value, bitmap).sendToTarget()
        }
    })

    private val handler = object : Handler(looperProvider.mainLooper()) {
        override fun handleMessage(msg: Message?) {
            if (msg != null &&
                    msg.what == PhotoDownloadState.COMPLETED.value &&
                    msg.obj != null) {

                    bitmap = Optional.of(msg.obj as Bitmap)
            }
        }
    }

    var bitmapDownloadListener: PhotoDownloadCompletedListener? = null

    fun cancel() {
        bitmapDownloadListener = null
        handler.removeCallbacksAndMessages(null)
        runnable.cancelDownload()
    }
}