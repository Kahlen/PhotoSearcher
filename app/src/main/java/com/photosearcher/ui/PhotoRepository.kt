package com.photosearcher.ui

import androidx.lifecycle.MutableLiveData
import com.photosearcher.list.data.BasePhotoViewModel
import com.photosearcher.list.data.LoadingViewModel
import com.photosearcher.list.data.PhotoViewModel
import com.photosearcher.network.download.PhotoTaskCreator
import com.photosearcher.network.search.data.PhotoSearchResponseData
import com.photosearcher.network.search.PhotoSearchApi
import com.photosearcher.network.search.PhotoSearchListener
import com.photosearcher.network.search.data.isLastPage
import com.photosearcher.ui.data.CompletedPhotoSearch
import com.photosearcher.ui.data.FailedPhotoSearch
import com.photosearcher.ui.data.InProgressPhotoSearch
import com.photosearcher.ui.data.PhotoSearchInfo
import com.photosearcher.ui.data.isLastPage
import com.photosearcher.util.SearchUrlConverter.url

/**
 * A class to search photos by text.
 */
class PhotoRepository constructor(
    private val searchApi: PhotoSearchApi,
    private val photoTaskCreator: PhotoTaskCreator
): PhotoSearchListener {

    @Volatile
    private var searchData: PhotoSearchInfo? = null
        @Synchronized set

    val photoData = MutableLiveData<List<BasePhotoViewModel>>()
    val error = MutableLiveData<Boolean>().apply { value = false }

    fun searchText(text: String) {
        if (text.isNotBlank() &&
                (text != searchData?.text || searchData?.status is FailedPhotoSearch)) {
            cancelCurrentDownloads()
            error.postValue(false)
            photoData.postValue(listOf(LoadingViewModel))
            searchApi.search(url(text, 1), text, this).also {
                searchData =
                    PhotoSearchInfo(text, InProgressPhotoSearch(1))
            }
        }
    }

    fun loadNextPage() {
        searchData?.let {
            if (it.status is CompletedPhotoSearch && !it.status.isLastPage()) {
                val nextPage = it.status.page+1
                searchApi.search(url(it.text, nextPage), it.text, this)
                searchData = PhotoSearchInfo(
                    it.text,
                    InProgressPhotoSearch(nextPage)
                )
            }
        }
    }

    override fun onSearchSucceeded(data: PhotoSearchResponseData) {
        if (data.searchText != searchData?.text) return

        error.postValue(false)
        searchData = PhotoSearchInfo(
            searchData?.text ?: "",
            CompletedPhotoSearch(data.page, data.totalPages)
        )
        val prefix = photoData.value?.dropLast(1) ?: emptyList()
        val suffix = if (data.isLastPage()) listOf() else listOf(LoadingViewModel)
        photoData.postValue(
            prefix +
            data.photoMetadata.map { metadata ->
            PhotoViewModel(
                id = metadata.id,
                title = metadata.title,
                photoTask = photoTaskCreator.create(metadata)
            )
        } + suffix)
    }

    override fun onSearchFailed(searchText: String) {
        if (searchText != searchData?.text) return

        searchData?.let {
            if (it.status is InProgressPhotoSearch) {
                if (it.status.page <= 1) {
                    photoData.postValue(listOf())
                    // show an error message
                    error.postValue(true)
                } else {
                    // remove loading image at the end
                    photoData.postValue(photoData.value?.dropLast(1))
                }
            }
        }
        searchData = PhotoSearchInfo(
            searchData?.text ?: "",
            FailedPhotoSearch
        )
    }

    /**
     * Clear all the data, and cancel search and download works.
     */
    fun clearAll() {
        cancelCurrentDownloads()
        photoData.value = listOf()
        searchData = null
        error.value = false
    }

    private fun cancelCurrentDownloads() {
        searchApi.cancelSearch()
        photoData.value?.filterIsInstance<PhotoViewModel>()?.forEach { it.photoTask.cancel() }
    }
}