package com.bignerdranch.android.photogalery

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterAndHolder(
    var photoList: List<GalleryItem>,
    private val thumbnail: ThumbnailDownloader<AdapterAndHolder.Holder>,
    private val fragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (photoList.isEmpty()) 0
        else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_galery, parent, false)
            Holder(view as ImageView)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.progressbar_layout, parent, false)
            Holder2(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Holder -> {
//                thumbnail.queueThumbnail(holder, photoList[position].url)
                holder.bind2(photoList[position])
                holder.initializeGalleryItem(photoList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return if (photoList.isEmpty()) 1
        else photoList.size
    }

    inner class Holder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        lateinit var galleryItem: GalleryItem

        fun bind(photo: Bitmap) {
//            imageView.setImageBitmap(photo)
//            imageView.setOnClickListener {
//                val intent = PhotoPageActivity.newIntent(fragment.requireContext(),galleryItem.photoPageUri)
//                fragment.startActivity(intent)
//            }
        }

        fun bind2(photoItem: GalleryItem){
            Picasso.get().load(photoItem.url).into(imageView)
            imageView.setOnClickListener {
                val intent = PhotoPageActivity.newIntent(fragment.requireContext(),galleryItem.photoPageUri)
                fragment.startActivity(intent)
            }
        }

        fun initializeGalleryItem(galleryItem: GalleryItem) {
            this.galleryItem = galleryItem
        }

    }

    inner class Holder2(view: View) : RecyclerView.ViewHolder(view) {

    }

}