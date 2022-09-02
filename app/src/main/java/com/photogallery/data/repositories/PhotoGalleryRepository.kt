package com.photogallery.data.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.photogallery.data.api.FlickrApi
import com.photogallery.data.api.FlickrResponse
import com.photogallery.domain.Photo
import com.photogallery.presentation.utils.CallbackImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PhotoGalleryRepository {

    private var flickrApi: FlickrApi

    init {
        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    private fun getPhotos(call: Call<FlickrResponse>): MutableLiveData<List<Photo>> {
        val photosLiveData = MutableLiveData<List<Photo>>()
        call.enqueue(object : CallbackImpl<FlickrResponse>() {
            override fun onResponse(
                call: Call<FlickrResponse>,
                response: Response<FlickrResponse>
            ) {
                val responseBody = response.body()
                val response = responseBody?.response
                val photoList = response?.photos?.filter { it.url_s.isNotBlank() } ?: emptyList()
                photosLiveData.value = photoList
            }
        })
        return photosLiveData
    }

    @WorkerThread
    fun getPhotoBytes(url: String): Bitmap? {
        val response = flickrApi.getPhotoBytes(url).execute()
        return response.body()?.byteStream().use(BitmapFactory::decodeStream)
    }

    fun getSearchPhotos(query: String): MutableLiveData<List<Photo>> {
        return getPhotos(getSearchPhotosCall(query))
    }

    fun getPhotos(): MutableLiveData<List<Photo>> {
        return getPhotos(getPhotosCall())
    }

    fun getPhotosCall(): Call<FlickrResponse>{
        return flickrApi.getPhotos()
    }

    fun getSearchPhotosCall(query: String): Call<FlickrResponse>{
        return flickrApi.searchPhotos(query)
    }
}