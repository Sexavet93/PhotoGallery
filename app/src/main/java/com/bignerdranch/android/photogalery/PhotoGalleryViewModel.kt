package com.bignerdranch.android.photogalery

import android.app.Application
import android.content.Context
import androidx.lifecycle.*

class PhotoGalleryViewModel(private val app: Application): AndroidViewModel(app) {

     val word: String
     get() = mutableSearchTerm.value ?: ""
     val galleryItemLiveData: LiveData<List<GalleryItem>>
     private val flickrFetch = FlickrFetch()
     private val mutableSearchTerm = MutableLiveData<String>()

     init {
          mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
          galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) {
               if(it.isBlank()){ flickrFetch.fetchPhotos() }
               else flickrFetch.searchPhotos(it)
          }
     }

     fun fetchPhotos(query: String = "") {
          mutableSearchTerm.value = query
          QueryPreferences.setStoredQuery(app,query)
     }

}