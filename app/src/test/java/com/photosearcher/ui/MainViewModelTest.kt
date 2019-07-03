package com.photosearcher.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.photosearcher.list.data.BasePhotoViewModel
import com.photosearcher.list.data.LoadingViewModel
import com.photosearcher.list.data.PhotoViewModel
import com.photosearcher.network.download.PhotoTask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: PhotoRepository

    private lateinit var viewModel: MainViewModel

    private val photoData = MutableLiveData<List<BasePhotoViewModel>>()
    private val error = MutableLiveData<Boolean>()

    @Before
    fun setUp() {
        `when`(repository.photoData).thenReturn(photoData)
        `when`(repository.error).thenReturn(error)
        viewModel = MainViewModel(repository)
    }

    @Test
    fun testSearch() {
        val searchText = "kittens"
        viewModel.search(searchText)
        verify(repository).searchText(searchText)
    }

    @Test
    fun testLoadNextPage() {
        viewModel.loadNextPage()
        verify(repository).loadNextPage()
    }

    @Test
    fun testPhotoData() {
        val photoViewModel1 = PhotoViewModel("title1", "id1", mock(PhotoTask::class.java))
        val photoViewModel2 = PhotoViewModel("title2", "id2", mock(PhotoTask::class.java))

        photoData.value = listOf(photoViewModel1, photoViewModel2, LoadingViewModel)
        assertEquals(viewModel.photos.value, listOf(photoViewModel1, photoViewModel2, LoadingViewModel))
    }

    @Test
    fun testError() {
        error.value = true
        assertTrue(viewModel.error.value!!)
    }
}