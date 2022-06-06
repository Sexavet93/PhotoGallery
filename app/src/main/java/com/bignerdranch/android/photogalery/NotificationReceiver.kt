package com.bignerdranch.android.photogalery

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val requestCode = intent?.getIntExtra(PollWorker.REQUEST_CODE,0)
            val notification = intent?.getParcelableExtra<Notification>(PollWorker.NOTIFICATION)
            NotificationManagerCompat.from(context!!).notify(requestCode!!,notification!!)
        }
    }
}