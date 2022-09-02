package com.photogallery.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.photogallery.R
import com.photogallery.data.repositories.PhotoGalleryRepository
import com.photogallery.presentation.activities.MainActivity
import com.photogallery.presentation.fragments.PhotoGalleryFragment

const val NOTIFICATION_CHANNEL_ID = "flickr_poll"

class NewPhotoWorker(private val context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    private val preferences = QueryPreferences.getInstance()
    private val photoRepository = PhotoGalleryRepository

    override fun doWork(): Result {
        val lastQuery = preferences.getLastQuery()
        val lastPhotoId = preferences.getLastPhotoId()
        val photos = if(lastQuery.isEmpty()){
            photoRepository.getPhotosCall().execute().body()?.response?.photos
        } else {
            photoRepository.getSearchPhotosCall(lastQuery).execute().body()?.response?.photos
        } ?: emptyList()

        if(photos.isEmpty()) return Result.success()
        val newPhotoId = photos.first().id
        if(newPhotoId != lastPhotoId){
            preferences.setLastPhotoId(newPhotoId)
            createNotification()
        }
        return Result.success()
    }

    private fun createNotification(){
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val resources = context.resources
        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_pictures_title))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_pictures_title))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        showBackgroundNotification(REQUEST_CODE, notification)
    }


    private fun showBackgroundNotification(requestCode: Int, notification: Notification){
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE_KEY,requestCode)
            putExtra(NOTIFICATION,notification)
        }
        context.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

    companion object{
        const val ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE"
        const val REQUEST_CODE_KEY = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
        const val REQUEST_CODE = 0
        const val NOTIFICATION_CHANNEL_NAME = "FlickrFetch"
    }

}