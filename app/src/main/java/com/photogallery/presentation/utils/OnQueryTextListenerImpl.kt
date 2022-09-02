package com.photogallery.presentation.utils

import androidx.appcompat.widget.SearchView

abstract class OnQueryTextListenerImpl: SearchView.OnQueryTextListener {

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}