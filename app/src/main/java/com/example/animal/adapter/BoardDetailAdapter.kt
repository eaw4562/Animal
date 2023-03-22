package com.example.animal.adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class BoardDetailPagerAdapter(private val context: Context, private val imageUrls: List<String>) :
    RecyclerView.Adapter<BoardDetailPagerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(com.example.animal.R.id.board_detail_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.example.animal.R.layout.fragment_board_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Glide를 사용하여 이미지를 로드합니다.
        Glide.with(context)
            .load(imageUrls[position])
            .into(holder.imageView)
    }

    override fun getItemCount() = imageUrls.size
}