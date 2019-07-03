package com.photosearcher.util

import java.net.URL

object SearchUrlConverter {
    private const val searchUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=1&text=%s&page=%d"
    fun url(text: String, page: Int): URL {
        return URL(String.format(searchUrl, text, page))
    }
}