package com.photosearcher.list.data

import com.photosearcher.network.download.PhotoTask

sealed class BasePhotoViewModel

object LoadingViewModel: BasePhotoViewModel()

/**
 * The data to be displayed on the UI.
 */
data class PhotoViewModel(
    val id: String,
    val title: String?,
    val photoTask: PhotoTask
): BasePhotoViewModel()