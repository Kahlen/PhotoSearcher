package com.photosearcher.ui

import androidx.lifecycle.ViewModel

/**
 * The ViewModel for MainActivity.
 */
class MainViewModel(private val repository: PhotoRepository): ViewModel() {

    val photos = repository.photoData
    val error = repository.error

    fun loadNextPage() {
        repository.loadNextPage()
    }

    fun search(text: String) {
        repository.searchText(text)
    }

    override fun onCleared() {
        repository.clearAll()
        super.onCleared()
    }
}