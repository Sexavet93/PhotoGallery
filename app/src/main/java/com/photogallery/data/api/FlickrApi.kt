package com.photogallery.data.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

private const val KEY = "cf030c434ae77f2512201cc9c6cc2b73"

interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList&api_key=$KEY&format=json&nojsoncallback=1&extras=url_s")
    fun getPhotos(): Call<FlickrResponse>

    @GET
    fun getPhotoBytes(@Url url: String): Call<ResponseBody>

    @GET("services/rest/?method=flickr.photos.search&api_key=$KEY&format=json&nojsoncallback=1&extras=url_s")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse>
}