package com.photosearcher.network.search

import com.photosearcher.network.search.data.PhotoMetadata
import com.photosearcher.network.search.data.PhotoSearchResponseData
import com.photosearcher.util.JsonUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

@RunWith(MockitoJUnitRunner::class)
class PhotoSearchRunnableTest {

    @Mock
    private lateinit var url: URL
    @Mock
    private lateinit var connection: HttpURLConnection
    @Mock
    private lateinit var listener: PhotoSearchListener
    @Mock
    private lateinit var jsonUtils: JsonUtils

    private lateinit var photoSearchRunnable: PhotoSearchRunnable
    private val responseInputStream = ByteArrayInputStream(Charset.forName("UTF-8").encode(responseString).array())
    private val photoSearchResponseData = PhotoSearchResponseData(
        page = 1,
        totalPages = 10,
        searchText = searchText,
        photoMetadata = listOf(PhotoMetadata(1, "server1", "id1", "secret1", "title1"),
            PhotoMetadata(2, "server2", "id2", "secret2", "title2")))

    @Before
    fun setUp() {
        `when`(url.openConnection()).thenReturn(connection)
        `when`(connection.inputStream).thenReturn(responseInputStream)
        `when`(jsonUtils.toPhotoSearchResponseData(anyString(), anyString())).thenReturn(photoSearchResponseData)
        photoSearchRunnable = PhotoSearchRunnable(url, searchText, listener, jsonUtils)
    }

    @Test
    fun testRun_succeeded() {
        photoSearchRunnable.run()
        verify(listener).onSearchSucceeded(photoSearchResponseData)
    }

    @Test
    fun testRun_failed() {
        `when`(jsonUtils.toPhotoSearchResponseData(anyString(), anyString())).thenReturn(null)
        photoSearchRunnable.run()
        verify(listener).onSearchFailed(searchText)
    }

    companion object {
        private const val searchText = "text"
        private const val responseString = """{"photos": {
            "page": 1,
            "pages": 10,
            "photo": [
            {
            "id": "id1",
            "secret": "secret1",
            "server": "server1",
            "farm": 1,
            "title": "title1"
            },
            {
            "id": "id2",
            "secret": "secret2",
            "server": "server2",
            "farm": 2,
            "title": "title2"
            }]}}"""
    }
}