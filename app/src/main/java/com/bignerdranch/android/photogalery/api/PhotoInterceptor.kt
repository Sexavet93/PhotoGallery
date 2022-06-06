package com.bignerdranch.android.photogalery.api

import okhttp3.Interceptor
import okhttp3.Response

private const val KEY = "cf030c434ae77f2512201cc9c6cc2b73"

class PhotoInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = originalRequest.url().newBuilder()
            .addQueryParameter("&api_key", KEY)
            .addQueryParameter("&format","json")
            .addQueryParameter("&nojsoncallback","1")
            .addQueryParameter("&extras","url_s")
            .addQueryParameter("&safesearch", "1")
            .build()

        val newRequest = originalRequest.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}