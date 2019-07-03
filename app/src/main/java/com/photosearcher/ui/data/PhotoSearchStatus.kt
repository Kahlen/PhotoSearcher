package com.photosearcher.ui.data

sealed class PhotoSearchStatus

data class InProgressPhotoSearch(
    val page: Int
): PhotoSearchStatus()

data class CompletedPhotoSearch(
    val page: Int,
    val totalPage: Int
): PhotoSearchStatus()

object FailedPhotoSearch : PhotoSearchStatus()

fun PhotoSearchStatus.isLastPage(): Boolean {
    return this is CompletedPhotoSearch && page == totalPage
}