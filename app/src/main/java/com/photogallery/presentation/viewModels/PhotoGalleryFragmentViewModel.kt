package com.photogallery.presentation.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.photogallery.data.NewPhotoWorker
import com.photogallery.data.QueryPreferences
import com.photogallery.data.repositories.PhotoGalleryRepository
import com.photogallery.domain.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

private const val POLL_WORK = "new_photo_worker"

class PhotoGalleryFragmentViewModel: ViewModel() {

    private val photoRepository = PhotoGalleryRepository
    val preferences = QueryPreferences.getInstance()

    private var queryLiveData = MutableLiveData<String>()
    val photoListLiveData: LiveData<List<Photo>>

    init {
        queryLiveData.value = getLastQuery()
        photoListLiveData = Transformations.switchMap(queryLiveData){
            if(it.isNotBlank()){
                photoRepository.getSearchPhotos(it)
            } else {
                photoRepository.getPhotos()
            }
        }
    }

    fun getSearchPhotos(query: String){
        if(query.isNotBlank()){
            queryLiveData.value = query
            preferences.setLastQuery(query)
        }
    }

    fun clearQuery(): Boolean{
        if(preferences.getLastQuery() != ""){
            queryLiveData.value = ""
            preferences.setLastQuery("")
            return true
        } else return false
    }

    private fun getLastQuery(): String{
        return preferences.getLastQuery()
    }

    fun createWorker(context: Context) {
        if(!preferences.getIsNotificationOn()){
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
            val periodicRequest = PeriodicWorkRequest.Builder(NewPhotoWorker::class.java, 15, TimeUnit.MINUTES).setConstraints(constraints).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(POLL_WORK, ExistingPeriodicWorkPolicy.KEEP, periodicRequest)
            preferences.setIsNotificationOn(true)
        }
    }

    fun removeWorker(context: Context){
        if(preferences.getIsNotificationOn()){
            WorkManager.getInstance(context).cancelUniqueWork(POLL_WORK)
            preferences.setIsNotificationOn(false)
        }
    }

}