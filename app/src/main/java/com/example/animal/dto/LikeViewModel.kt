package com.example.animal.dto

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LikeViewModel : ViewModel() {
    private val mDbRef = FirebaseFirestore.getInstance()

    fun addLike(contentUid : String, userId: String){
        mDbRef.collection("images")
            .document(contentUid)
            .collection("likes")
            .document(userId)
            .set(mapOf("liked" to true))
    }

    fun removeLike(contentUid: String, userId: String){
        mDbRef.collection("images")
            .document(contentUid)
            .collection("likes")
            .document(userId)
            .delete()
    }
}
