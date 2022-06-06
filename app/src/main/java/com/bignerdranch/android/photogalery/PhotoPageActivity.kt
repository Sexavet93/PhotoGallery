package com.bignerdranch.android.photogalery

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class PhotoPageActivity : AppCompatActivity() {

    lateinit var photoPageFragment: PhotoPageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)
        if(savedInstanceState == null){
            photoPageFragment = PhotoPageFragment.newInstance(intent.data)
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,photoPageFragment).commit()
        }

    }

    companion object{
        fun newIntent(context: Context,photoPageUri: Uri): Intent{
            return Intent(context,PhotoPageActivity::class.java).apply { data = photoPageUri }
        }
    }

    override fun onBackPressed() {
        val webView = photoPageFragment.getWebView()
        if(webView.canGoBack()){
            webView.goBack()
        }else return super.onBackPressed()
    }
}