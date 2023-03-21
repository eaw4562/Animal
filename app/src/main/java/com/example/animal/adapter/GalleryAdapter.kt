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

class GalleryAdapter() : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    lateinit var imageList: ArrayList<Uri>
    lateinit var context : Context

    constructor(imageList: ArrayList<Uri>, context: Context): this(){
        this.imageList = imageList
        this.context = context
    }
    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.multi_image_item, parent,false)
        return ViewHolder(view)

    }

    //데이터 설정
    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int) {
        Glide.with(context)
            .load(imageList[position])//이미지 위치
            .into(holder.galleryView)
    }

    //아이템 갯수
    override fun getItemCount(): Int {
        return imageList.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val galleryView: ImageView = view.findViewById(R.id.multi_image)
    }
}