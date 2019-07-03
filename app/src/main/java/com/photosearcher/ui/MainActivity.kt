package com.photosearcher.ui

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photosearcher.databinding.ActivityMainBinding
import com.photosearcher.list.PhotoListAdapter
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.photosearcher.R
import com.photosearcher.list.data.BasePhotoViewModel
import com.photosearcher.network.download.PhotoTaskCreator
import com.photosearcher.network.search.PhotoSearchApi
import com.photosearcher.util.BitmapUtils
import com.photosearcher.util.JsonUtils
import com.photosearcher.util.LooperProvider

class MainActivity : AppCompatActivity() {

    private lateinit var bindingView: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory()).get(MainViewModel::class.java)
        initUi()
    }

    private fun initUi() {
        bindingView.searchEditText.setOnEditorActionListener { textView, actionId, _ ->
            hideKeyboard()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = textView.text.toString()
                if (text.isNotBlank()) {
                    viewModel.search(text)
                    true
                }
            }
            false
        }
        bindingView.searchButton.setOnClickListener {
            val text = bindingView.searchEditText.text.toString()
            if (text.isNotBlank()) {
                viewModel.search(text)
                hideKeyboard()
            }
        }

        val layoutManager = GridLayoutManager(this, columnSize)
        val adapter = PhotoListAdapter()
        bindingView.photoList.hasFixedSize()
        bindingView.photoList.layoutManager = layoutManager
        bindingView.photoList.adapter = adapter

        bindingView.photoList.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount-1) {
                    viewModel.loadNextPage()
                }
            }
        })

        viewModel.photos.observe(this,
            Observer<List<BasePhotoViewModel>> { t -> adapter.submitList(t) })
        viewModel.error.observe(this, Observer<Boolean> { error ->
            bindingView.errorView.visibility = if (error) View.VISIBLE else View.GONE
        })
    }

    private fun viewModelFactory(): ViewModelProvider.Factory {
        return object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    return MainViewModel(
                        PhotoRepository(
                            PhotoSearchApi.getInstance(JsonUtils()),
                            PhotoTaskCreator(LooperProvider(), BitmapUtils()))) as T
                }
                throw IllegalArgumentException("Unknown model type ${modelClass.simpleName}")
            }

        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(bindingView.searchEditText.windowToken, 0)
    }
    
    companion object {
        private const val columnSize = 3
    }
}
