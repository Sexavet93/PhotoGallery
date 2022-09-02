package com.photogallery.domain

data class Photo(
    var id: String = "",
    var owner: String = "",
    var title: String = "",
    var url_s: String = ""
){
    val photoPageUri: String
        get() = "https://www.flickr.com/photos/$owner/$id".replace("@", "%40")
}
