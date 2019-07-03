package com.photosearcher.list

import androidx.recyclerview.widget.DiffUtil
import com.photosearcher.list.data.BasePhotoViewModel
import com.photosearcher.list.data.LoadingViewModel
import com.photosearcher.list.data.PhotoViewModel

/**
 * An utility class to determine how the list view should be reloaded when the list changes.
 */
class PhotoDiff: DiffUtil.ItemCallback<BasePhotoViewModel>() {

    override fun areItemsTheSame(oldItem: BasePhotoViewModel, newItem: BasePhotoViewModel): Boolean {
        return (oldItem is LoadingViewModel && newItem is LoadingViewModel) ||
            (oldItem is PhotoViewModel && newItem is PhotoViewModel && oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: BasePhotoViewModel, newItem: BasePhotoViewModel): Boolean {
        return (oldItem is LoadingViewModel && newItem is LoadingViewModel) ||
            (oldItem is PhotoViewModel && newItem is PhotoViewModel &&
                (oldItem.id == newItem.id &&
                oldItem.title == newItem.title &&
                oldItem.photoTask.bitmap == newItem.photoTask.bitmap))
    }

}