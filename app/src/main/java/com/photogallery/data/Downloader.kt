package com.photogallery.data

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import com.photogallery.data.repositories.PhotoGalleryRepository
import com.photogallery.presentation.adapters.PhotoGalleryAdapter
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "downloader"
private const val DOWNLOAD = 1

class Downloader<T : Any>: HandlerThread(TAG), LifecycleObserver {

    private val responseHandler: Handler = Handler(Looper.getMainLooper())
    private val repository = PhotoGalleryRepository
    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()

    val lifecycleObserver = LifecycleEventObserver { _, event ->
        when(event){
            Lifecycle.Event.ON_CREATE -> start()
            Lifecycle.Event.ON_DESTROY -> quit()
        }
    }

    val viewLifecycleObserver = LifecycleEventObserver { _, event ->
        when(event){
            Lifecycle.Event.ON_DESTROY -> {
                requestHandler.removeMessages(DOWNLOAD)
                requestMap.clear()
            }
        }
    }

    fun putMessage(target: T, url: String){
        requestMap[target] = url
        requestHandler.obtainMessage(DOWNLOAD,target).sendToTarget()
    }

    override fun onLooperPrepared() {
        requestHandler = object : Handler(looper){
            override fun handleMessage(msg: Message) {
                if(msg.what == DOWNLOAD){
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T){
        val url = requestMap[target] ?: return
        val photoBitmap = repository.getPhotoBytes(url)
        onMainThreadWorker(url, target, photoBitmap)
    }

    private fun onMainThreadWorker(url: String, target: T, photoBitmap: Bitmap?){
        responseHandler.post{
            if(requestMap[target] != url || hasQuit){
                return@post
            }
            (target as PhotoGalleryAdapter.PhotoHolder).bind(photoBitmap)
            requestMap.remove(target)
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }
}