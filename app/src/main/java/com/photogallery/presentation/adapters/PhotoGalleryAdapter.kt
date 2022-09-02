package com.photogallery.presentation.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.photogallery.R
import com.photogallery.data.Downloader
import com.photogallery.databinding.PhotoItemBinding
import com.photogallery.domain.Photo

class PhotoGalleryAdapter(
    private val downloader: Downloader<PhotoGalleryAdapter.PhotoHolder>,
    private val listener: (url: String) -> Unit
    ) : RecyclerView.Adapter<PhotoGalleryAdapter.PhotoHolder>() {

    var photoList: List<Photo> = emptyList()
    set(value){
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGalleryAdapter.PhotoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item,parent,false)
        return PhotoHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoGalleryAdapter.PhotoHolder, position: Int) {
        val photo = photoList[position]
        downloader.putMessage(holder, photo.url_s)

        holder.binding.imageView.setOnClickListener{
            listener.invoke(photo.photoPageUri)
        }
    }

    override fun getItemCount() = photoList.size

    inner class PhotoHolder(view: View): RecyclerView.ViewHolder(view){

        val binding: PhotoItemBinding = PhotoItemBinding.bind(view)

        fun bind(photo: Bitmap?){
            binding.apply {
                progressBar.visibility = View.GONE
                imageView.setImageBitmap(photo)
            }

        }
    }
}