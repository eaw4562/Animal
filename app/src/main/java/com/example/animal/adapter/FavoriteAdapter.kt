package com.example.animal.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animal.BoardDetailFragment
import com.example.animal.dto.Item
import com.example.animal.R
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.util.*

class FavoriteAdapter(private val itemList: MutableList<Item>) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        val priceFormat = NumberFormat.getNumberInstance(Locale.US)
        val formattedPrice = priceFormat.format(item.price?.toLong())
        holder.price.text = formattedPrice
        holder.loadImage(item.imageUrl)
        holder.itemView.setOnClickListener {
            val fragment = BoardDetailFragment.newInstance(item.contentUid, item.imageUrl)
            val bundle = Bundle()
            bundle.putString("contentUid", item.contentUid)
            bundle.putString("uid",item.uid)
            bundle.putString("title", item.title)
            fragment.arguments = bundle
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
        var image: ImageView = itemView.findViewById(R.id.favorite_item_image)
        var title: TextView = itemView.findViewById(R.id.favorite_item_title)
        var price: TextView = itemView.findViewById(R.id.favorite_item_price)

        fun loadImage(url: String) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(itemView.context)
                    .load(uri)
                    .into(image)
            }
        }
    }
}
