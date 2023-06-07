package com.example.animal.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animal.R

class GalleryAdapter(
    private val imageList: ArrayList<Uri>,
    private val context: Context
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.multi_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = imageList[position]
        val layoutParams = holder.galleryView.layoutParams
        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.image_width)
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        holder.itemView.layoutParams = layoutParams

        Glide.with(context)
            .load(imageList[position])
            .into(holder.galleryView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val galleryView: ImageView = view.findViewById(R.id.multi_image)
    }
}
