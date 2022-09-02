package com.photogallery.presentation.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.photogallery.data.NewPhotoWorker

abstract class VisibleFragment: Fragment() {

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(NewPhotoWorker.ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(receiver,filter,NewPhotoWorker.PERM_PRIVATE,null)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(receiver)
    }
}