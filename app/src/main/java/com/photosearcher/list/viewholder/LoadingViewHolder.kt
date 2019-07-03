package com.photosearcher.list.viewholder

import android.view.View
import com.photosearcher.list.data.BasePhotoViewModel

/**
 * An NO-OP view holder.
 */
class LoadingViewHolder(view: View): BaseViewHolder(view) {
    override fun bindModel(model: BasePhotoViewModel) {
    }
}