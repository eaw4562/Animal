package com.example.animal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animal.BoardDetailFragment
import com.example.animal.DTO.Item
import com.example.animal.R
import com.google.firebase.storage.FirebaseStorage

class BoardAdapter(private val itemList: MutableList<Item>) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.price.text = item.price
        // Firebase Storage에서 이미지 가져오기
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.imageUrl)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.itemView.context)
                .load(uri)
                .into(holder.image)
        }
        holder.itemView.setOnClickListener {
            val fragment = BoardDetailFragment.newInstance(item.contentUid, item.imageUrl)
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }



    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.main_item_image)
        var title: TextView = itemView.findViewById(R.id.main_item_title)
        var price: TextView = itemView.findViewById(R.id.main_item_price)
    }
}