package com.photogallery.data.api

import com.google.gson.annotations.SerializedName

data class FlickrResponse(
    @SerializedName("photos")
    val response: Photos
)
