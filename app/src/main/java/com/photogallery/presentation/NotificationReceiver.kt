package com.photogallery.presentation

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.photogallery.data.NewPhotoWorker

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val requestCode = intent?.getIntExtra(NewPhotoWorker.REQUEST_CODE_KEY, 0) ?: 0
            val notification = intent?.getParcelableExtra<Notification>(NewPhotoWorker.NOTIFICATION)
            notification?.let { NotificationManagerCompat.from(context!!).notify(requestCode,it) }
        }
    }
}