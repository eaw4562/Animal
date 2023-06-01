package com.example.animal.dto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LikeViewModel : ViewModel() {
    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean> get() = _isLiked

    fun toggleLikeStatus() {
        _isLiked.value = _isLiked.value != true
    }
}
