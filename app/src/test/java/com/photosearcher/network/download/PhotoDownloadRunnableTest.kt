package com.photosearcher.network.download

import android.graphics.Bitmap
import com.photosearcher.util.BitmapUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.JarURLConnection
import java.net.URL
import java.nio.charset.Charset

@RunWith(MockitoJUnitRunner::class)
class PhotoDownloadRunnableTest {

    @Mock
    private lateinit var listener: PhotoDownloadStateChangeListener
    @Mock
    private lateinit var bitmapUtils: BitmapUtils
    @Mock
    private lateinit var bitmap: Bitmap
    @Mock
    private lateinit var url: URL
    @Mock
    private lateinit var connection: HttpURLConnection

    private lateinit var runnable: PhotoDownloadRunnable

    @Before
    fun setUp() {
        `when`(url.openConnection()).thenReturn(connection)
        val inputStream = ByteArrayInputStream(
            Charset.forName("UTF-8").encode("something").array())
        `when`(connection.inputStream).thenReturn(inputStream)
        `when`(bitmapUtils.toBitmap(inputStream)).thenReturn(bitmap)
        runnable = PhotoDownloadRunnable(url, bitmapUtils, listener)
    }

    @Test
    fun testRun_succeeded() {
        runnable.run()
        verify(listener).onPhotoDownloadStateChanged(PhotoDownloadState.DOWNLOADING)
        verify(listener).onPhotoDownloadStateChanged(PhotoDownloadState.COMPLETED, bitmap)
    }

    @Test
    fun testRun_failed() {
        `when`(url.openConnection()).thenReturn(mock(JarURLConnection::class.java))
        runnable.run()
        verify(listener).onPhotoDownloadStateChanged(PhotoDownloadState.DOWNLOADING)
        verify(listener).onPhotoDownloadStateChanged(PhotoDownloadState.FAILED)
    }
}