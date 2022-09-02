package com.photogallery.data.api

import com.google.gson.annotations.SerializedName
import com.photogallery.domain.Photo

data class Photos(
    @SerializedName("photo")
    val photos: List<Photo>
)
