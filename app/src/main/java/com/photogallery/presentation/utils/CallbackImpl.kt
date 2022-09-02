package com.photogallery.presentation.utils

import retrofit2.Call
import retrofit2.Callback

abstract class CallbackImpl<T>: Callback<T> {

    override fun onFailure(call: Call<T>, t: Throwable) {

    }
}