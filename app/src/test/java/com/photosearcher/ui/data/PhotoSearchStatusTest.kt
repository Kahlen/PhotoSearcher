package com.photosearcher.ui.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoSearchStatusTest {

    @Test
    fun testIsLastPage_lastPage() {
        assertTrue(CompletedPhotoSearch(10, 10).isLastPage())
    }

    @Test
    fun testIsLastPage_notLastPage() {
        assertFalse(CompletedPhotoSearch(6, 10).isLastPage())
    }

    @Test
    fun testIsLastPage_inProgress() {
        assertFalse(InProgressPhotoSearch(3).isLastPage())
    }

    @Test
    fun testIsLastPage_failed() {
        assertFalse(FailedPhotoSearch.isLastPage())
    }
}