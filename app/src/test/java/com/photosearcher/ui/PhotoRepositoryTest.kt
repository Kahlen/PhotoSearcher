package com.photosearcher.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.photosearcher.list.data.LoadingViewModel
import com.photosearcher.list.data.PhotoViewModel
import com.photosearcher.network.download.PhotoTask
import com.photosearcher.network.download.PhotoTaskCreator
import com.photosearcher.network.search.PhotoSearchApi
import com.photosearcher.network.search.data.PhotoMetadata
import com.photosearcher.network.search.data.PhotoSearchResponseData
import com.photosearcher.util.SearchUrlConverter.url
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PhotoRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var searchApi: PhotoSearchApi
    @Mock
    private lateinit var photoTaskCreator: PhotoTaskCreator

    private lateinit var repository: PhotoRepository

    @Before
    fun setUp() {
        `when`(photoTaskCreator.create(photoMetadata1)).thenReturn(photoTask1)
        `when`(photoTaskCreator.create(photoMetadata2)).thenReturn(photoTask2)
        repository = PhotoRepository(searchApi, photoTaskCreator)
    }

    @Test
    fun testSearchText_empty() {
        repository.searchText("")
        verifyZeroInteractions(searchApi)
    }

    @Test
    fun testSearchText_text() {
        val searchText = "elephants"
        repository.searchText(searchText)
        verify(searchApi).search(url(searchText, 1), searchText, repository)
    }

    @Test
    fun testSearchText_searchInProgress_sameText() {
        val searchText = "puppies"
        repository.searchText(searchText)
        verify(searchApi).search(url(searchText, 1), searchText, repository)
        repository.searchText(searchText)
        verify(searchApi).cancelSearch()
        // search only once
        verify(searchApi).search(url(searchText, 1), searchText, repository)
    }

    @Test
    fun testSearchText_searchInProgress_differentText_inProgress() {
        val searchText1 = "puppies"
        repository.searchText(searchText1)
        verify(searchApi).search(url(searchText1, 1), searchText1, repository)

        val searchText2 = "kittens"
        repository.searchText(searchText2)
        verify(searchApi).search(url(searchText2, 1), searchText2, repository)
    }

    @Test
    fun testSearchText_searchCompleted() {
        val searchText1 = "bears"
        repository.searchText(searchText1)
        verify(searchApi).search(url(searchText1, 1), searchText1, repository)
        val response =
            PhotoSearchResponseData(
                1,
                10,
                listOf(photoMetadata1, photoMetadata2),
                searchText1)
        repository.onSearchSucceeded(response)

        val searchText2 = "penguin"
        repository.searchText(searchText2)
        verify(searchApi).search(url(searchText2, 1), searchText2, repository)
        verify(searchApi, atLeastOnce()).cancelSearch()
        verify(photoTask1, atLeastOnce()).cancel()
        verify(photoTask2, atLeastOnce()).cancel()
    }

    @Test
    fun testSearchText_failedBefore() {
        val searchText = "kittens"
        repository.searchText(searchText)
        verify(searchApi).search(url(searchText, 1), searchText, repository)
        repository.onSearchFailed(searchText)
        repository.searchText(searchText)
        verify(searchApi, times(2)).search(url(searchText, 1), searchText, repository)
    }

    @Test
    fun testOnSearchSucceeded_hasMorePages() {
        val searchText = "test"
        repository.searchText(searchText)
        val response =
            PhotoSearchResponseData(
                1,
                10,
                listOf(photoMetadata1, photoMetadata2),
                searchText)
        repository.onSearchSucceeded(response)
        assertEquals(
            listOf(
                PhotoViewModel(photoMetadata1.id, photoMetadata1.title, photoTask1),
                PhotoViewModel(photoMetadata2.id, photoMetadata2.title, photoTask2),
                LoadingViewModel),
            repository.photoData.value)
        assertFalse(repository.error.value!!)
    }

    @Test
    fun testOnSearchSucceeded_lastPage() {
        val searchText = "test"
        repository.searchText(searchText)
        val response =
            PhotoSearchResponseData(
                10,
                10,
                listOf(photoMetadata1, photoMetadata2),
                searchText)
        repository.onSearchSucceeded(response)
        // doesn't have LoadingViewModel
        assertEquals(
            listOf(
                PhotoViewModel(photoMetadata1.id, photoMetadata1.title, photoTask1),
                PhotoViewModel(photoMetadata2.id, photoMetadata2.title, photoTask2)),
            repository.photoData.value)
        assertFalse(repository.error.value!!)
    }

    @Test
    fun testOnSearchFailed() {
        val searchText = "text"
        repository.searchText(searchText)
        repository.onSearchFailed(searchText)
        assertTrue(repository.error.value!!)
        assertTrue(repository.photoData.value!!.isEmpty())
    }

    @Test
    fun testOnSearchFailed_afterSucceeded() {
        val searchText = "text"
        repository.searchText(searchText)
        val response =
            PhotoSearchResponseData(
                1,
                10,
                listOf(photoMetadata1, photoMetadata2),
                searchText)
        repository.onSearchSucceeded(response)
        repository.loadNextPage()

        repository.onSearchFailed(searchText)
        // don't show error message because some photos were downloaded already
        assertFalse(repository.error.value!!)
        assertEquals(
            listOf(
                PhotoViewModel(photoMetadata1.id, photoMetadata1.title, photoTask1),
                PhotoViewModel(photoMetadata2.id, photoMetadata2.title, photoTask2)),
            repository.photoData.value)
    }

    @Test
    fun testLoadNextPage() {
        val searchText = "dogs"
        repository.searchText(searchText)
        val response =
            PhotoSearchResponseData(
                1,
                10,
                listOf(photoMetadata1, photoMetadata2),
                searchText)
        repository.onSearchSucceeded(response)

        repository.loadNextPage()
        verify(searchApi).search(url(searchText, 2), searchText, repository)
    }

    @Test
    fun testLoadNextPage_searchInProgress() {
        val searchText = "cats"
        repository.searchText(searchText)

        repository.loadNextPage()
        verify(searchApi).search(url(searchText, 1), searchText, repository)
        // don't search next page because the previous request is still in progress
        verify(searchApi, never()).search(url(searchText, 2), searchText, repository)
    }

    @Test
    fun testLoadNextPage_reachedLastPage() {
        val searchText = "parks"
        repository.searchText(searchText)
        val response =
            PhotoSearchResponseData(
                1,
                1,
                listOf(photoMetadata1, photoMetadata2),
                searchText)
        repository.onSearchSucceeded(response)

        repository.loadNextPage()
        verify(searchApi).search(url(searchText, 1), searchText, repository)
        // don't search next page because it already reached the last page
        verify(searchApi, never()).search(url(searchText, 2), searchText, repository)
    }

    @Test
    fun testClearAll() {
        repository.searchText("something")
        val response =
            PhotoSearchResponseData(
                1,
                10,
                listOf(photoMetadata1, photoMetadata2),
                "something")
        repository.onSearchSucceeded(response)

        repository.clearAll()
        assertTrue(repository.photoData.value!!.isEmpty())
        assertFalse(repository.error.value!!)
        verify(searchApi, atLeastOnce()).cancelSearch()
        verify(photoTask1, atLeastOnce()).cancel()
        verify(photoTask2, atLeastOnce()).cancel()
    }

    companion object {
        private val photoMetadata1 = PhotoMetadata(1, "server1", "id1", "secret1", "title1")
        private val photoMetadata2 = PhotoMetadata(2, "server2", "id2", "secret2", "title2")
        private val photoTask1 = mock(PhotoTask::class.java)
        private val photoTask2 = mock(PhotoTask::class.java)
    }
}