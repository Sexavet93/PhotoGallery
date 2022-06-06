package com.bignerdranch.android.photogalery.api

import com.bignerdranch.android.photogalery.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}