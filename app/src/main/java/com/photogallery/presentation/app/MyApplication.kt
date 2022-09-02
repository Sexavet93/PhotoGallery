package com.photogallery.presentation.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.photogallery.data.NOTIFICATION_CHANNEL_ID
import com.photogallery.data.NewPhotoWorker
import com.photogallery.data.QueryPreferences

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        QueryPreferences.initInstance(applicationContext)

        versionChecker()
    }

    private fun versionChecker(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NewPhotoWorker.NOTIFICATION_CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}