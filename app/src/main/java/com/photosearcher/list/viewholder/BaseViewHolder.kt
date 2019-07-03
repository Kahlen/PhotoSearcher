package com.photosearcher.list.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.photosearcher.list.data.BasePhotoViewModel

abstract class BaseViewHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bindModel(model: BasePhotoViewModel)
}