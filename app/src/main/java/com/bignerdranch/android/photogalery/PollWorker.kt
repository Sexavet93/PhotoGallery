package com.bignerdranch.android.photogalery

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class PollWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val flickrFetch = FlickrFetch()

    override fun doWork(): Result {
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context)
        val items = if (query.isEmpty()) {
            flickrFetch.fetchPhotosRequest().execute().body()?.photos?.galleryItems
        } else {
            flickrFetch.searchPhotosRequest(query).execute().body()?.photos?.galleryItems
        } ?: emptyList()
        if (items.isEmpty()) { return  Result.success() }
        if(items.first().id != lastResultId){
            QueryPreferences.setLastResultId(context,items.first().id)
            createNotification()
        }
        return Result.success()
    }

    private fun createNotification(){
        val intent = Intent(context,PhotoGalleryActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
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
        showBackgroundNotification(0,notification)
    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification){
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE,requestCode)
            putExtra(NOTIFICATION,notification)
        }
        context.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

    companion object{
        const val ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}