package com.bignerdranch.android.photogalery

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T : Any>(private val responseHandler: Handler) : HandlerThread(TAG), LifecycleObserver {

    private var hasQuite = false
    private val flickrFetch = FlickrFetch()
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    val fragmentLifecycleObserver = object : LifecycleObserver{
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup() {
            start()
            looper
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() {
            quit()
        }
    }
    val viewLifecycleObserver = object : LifecycleObserver{
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearQueue(){
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    fun queueThumbnail(target: T, url: String) {
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler(looper) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

     fun handleRequest(target: T){
        val url = requestMap[target] ?: return
        val bitmap = flickrFetch.fetchPhoto(url) ?: return
        responseHandler.post(Runnable {
            if(requestMap[target] != url || hasQuite){
                return@Runnable
            }
            (target as AdapterAndHolder.Holder).bind(bitmap)
            requestMap.remove(target)
        })
    }

    override fun quit(): Boolean {
        hasQuite = true
        return super.quit()
    }
}