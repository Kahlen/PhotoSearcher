package com.photosearcher.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream

class BitmapUtils {
    fun toBitmap(inputStream: InputStream): Bitmap? = BitmapFactory.decodeStream(inputStream)
}