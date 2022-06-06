package com.bignerdranch.android.photogalery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.photogalery.api.FlickrApi
import com.bignerdranch.android.photogalery.api.FlickrResponse
import com.bignerdranch.android.photogalery.api.PhotoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FlickrFetch{

    private var flickrApi: FlickrApi

    init{
//        val client = OkHttpClient.Builder().addInterceptor(PhotoInterceptor()).build()
        val retrofit = Retrofit.Builder().baseUrl("https://www.flickr.com/").addConverterFactory(GsonConverterFactory.create()).build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>) : LiveData<List<GalleryItem>>{
        val responseLiveData = MutableLiveData<List<GalleryItem>>()
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                val flickrResponse = response.body()
                val photoResponse = flickrResponse?.photos
                var galleryItems = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems = galleryItems.filterNot { it.url.isBlank() }
                responseLiveData.value = galleryItems
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {

            }
        })
        return responseLiveData
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>>{
        return fetchPhotoMetadata(fetchPhotosRequest())
    }

    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response = flickrApi.fetchUrlBytes(url).execute()
        return response.body()?.byteStream().use(BitmapFactory::decodeStream)

    }

    fun searchPhotos(query: String): LiveData<List<GalleryItem>>{
        return fetchPhotoMetadata(searchPhotosRequest(query))
    }

    fun fetchPhotosRequest(): Call<FlickrResponse>{
        return flickrApi.fetchPhotos()
    }

    fun searchPhotosRequest(query: String): Call<FlickrResponse>{
        return flickrApi.searchPhotos(query)
    }

}
