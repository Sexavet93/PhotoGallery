package com.photogallery.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.photogallery.R
import com.photogallery.presentation.fragments.PhotoGalleryFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}