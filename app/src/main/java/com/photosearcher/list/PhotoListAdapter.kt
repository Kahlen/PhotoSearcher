package com.photosearcher.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.photosearcher.R
import com.photosearcher.databinding.ViewPhotoBinding
import com.photosearcher.list.data.BasePhotoViewModel
import com.photosearcher.list.data.LoadingViewModel
import com.photosearcher.list.viewholder.BaseViewHolder
import com.photosearcher.list.viewholder.LoadingViewHolder
import com.photosearcher.list.viewholder.PhotoViewHolder

/**
 * The adapter for the photos.
 */
class PhotoListAdapter: ListAdapter<BasePhotoViewModel, BaseViewHolder>(PhotoDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING ->
                LoadingViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.view_loading,
                        parent,
                        false
                    )
                )
            VIEW_TYPE_PHOTO ->
                PhotoViewHolder(
                    ViewPhotoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            else ->
                throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindModel(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is LoadingViewModel) VIEW_TYPE_LOADING else VIEW_TYPE_PHOTO
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 1
        private const val VIEW_TYPE_PHOTO = 2
    }
}