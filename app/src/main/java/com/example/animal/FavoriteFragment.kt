package com.example.animal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.adapter.FavoriteAdapter
import com.example.animal.dto.ContentDTO
import com.example.animal.dto.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class FavoriteFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private var itemList: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.favorite_recycler) // replace with your RecyclerView's ID
        adapter = FavoriteAdapter(itemList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        loadFavoritedPosts()
    }

    private fun loadFavoritedPosts() {
        // Get the user's liked posts
        val userLikesDocument = firestore.collection("userLikes").document(auth.currentUser!!.uid)

        userLikesDocument.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val likedPosts = document["contentUid"] as? List<String> ?: listOf()
                fetchPosts(likedPosts)
            }
        }
    }

    private fun fetchPosts(likedPosts: List<String>) {
        // Get the information of each liked post
        for (postId in likedPosts) {
            val postDocument = firestore.collection("images").document(postId)
            postDocument.get().addOnSuccessListener { document ->
                if (itemList.isEmpty()) {
                    FirebaseFirestore.getInstance().collection("images")
                        .orderBy("timeStamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val contentDTO = document.toObject(ContentDTO::class.java)
                                if (contentDTO.title != null && contentDTO.price != null && contentDTO.imageUrl != null) {
                                    val item = Item(
                                        contentDTO.title,
                                        contentDTO.price,
                                        contentDTO.imageUrl!!,
                                        contentDTO.contentUid,
                                        contentDTO.age,
                                        contentDTO.breed,
                                        contentDTO.category,
                                        contentDTO.content,
                                        contentDTO.gender,
                                        contentDTO.name,
                                        contentDTO.price,
                                        contentDTO.spay,
                                        contentDTO.vaccine,
                                        contentDTO.where,
                                        contentDTO.uid
                                    )
                                    itemList.add(item)
                                }
                            }
                            adapter.notifyItemInserted(itemList.size - 1) // Notify the adapter that an item is inserted
                        }
                        .addOnFailureListener { exception ->
                            Log.e(HomeTapOneFragment.TAG, "Error getting documents: ", exception)
                        }
                }
            }
        }
    }
}
