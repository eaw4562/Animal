package com.example.animal.Adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animal.DTO.ContentDTO
import com.example.animal.R

class BoardDetailAdapter(private val boardList: List<ContentDTO>) :
    RecyclerView.Adapter<BoardDetailAdapter.BoardViewHolder>() {

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.board_detail_image)
        val title: TextView = itemView.findViewById(R.id.board_detail_title)
        val age : TextView = itemView.findViewById(R.id.board_detail_age)
        val gender :TextView = itemView.findViewById(R.id.board_detail_gender)
        var breed : TextView = itemView.findViewById(R.id.board_detail_breed)
        var category : TextView = itemView.findViewById(R.id.board_detail_category)
        var vaccine : TextView = itemView.findViewById(R.id.board_detail_vaccine)
        var where : TextView = itemView.findViewById(R.id.board_detail_where)
        var spay : TextView = itemView.findViewById(R.id.board_radio_spay)
        val content: TextView = itemView.findViewById(R.id.board_detail_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_board_detail, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board = boardList[position]
        (holder as BoardViewHolder).title.text = board.title
        holder.category.text = board.category
        holder.gender.text = board.gender
        holder.age.text = board.age
        holder.breed.text = board.breed
        holder.vaccine.text = board.vaccine
        holder.spay.text = board.spay
        holder.content.text = board.content

        // 이미지 URL 리스트를 ','로 분리하여 배열에 저장합니다.
        val imageUrlList = board.imageUrl?.split(",")?.toTypedArray()

        // 이미지 URL 리스트에서 첫 번째 이미지를 로드합니다.
        Glide.with(holder.itemView.context)
            .load(imageUrlList?.get(0))
            .into(holder.image)
    }

    override fun getItemCount() = boardList.size
}
