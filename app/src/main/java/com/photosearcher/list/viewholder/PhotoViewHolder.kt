package com.photosearcher.list.viewholder

import android.graphics.Bitmap
import com.photosearcher.databinding.ViewPhotoBinding
import com.photosearcher.list.data.BasePhotoViewModel
import com.photosearcher.list.data.PhotoViewModel
import com.photosearcher.network.download.PhotoDownloader

/**
 * The view holder to display photos.
 */
class PhotoViewHolder(private val bindingView: ViewPhotoBinding): BaseViewHolder(bindingView.root) {

    override fun bindModel(model: BasePhotoViewModel) {
        if (model !is PhotoViewModel) return
        bindingView.photoTitle.text = model.title ?: ""
        if (!model.photoTask.bitmap.isPresent) {
            model.photoTask.bitmapDownloadListener = object: PhotoDownloadCompletedListener {
                override fun bitmapDownloaded(bitmap: Bitmap?) {
                    model.photoTask.bitmapDownloadListener = null
                    bindingView.photoImageView.setImageBitmap(bitmap)
                }
            }
            PhotoDownloader.instance.download(model.photoTask)
        } else {
            bindingView.photoImageView.setImageBitmap(model.photoTask.bitmap.get())
        }
    }
}