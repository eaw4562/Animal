package com.example.animal.fcm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

class FirebaseRepository() {

    private val _myResponse: MutableLiveData<Response<ResponseBody>> = MutableLiveData() // 메세지 수신 정보
    val myResponse: LiveData<Response<ResponseBody>>
        get() = _myResponse

    // 푸시 메세지 전송
    suspend fun sendNotification(notification: NotificationBody) {
        try {
            _myResponse.value = RetrofitInstance.api.sendNotification(notification)
        } catch (e: IOException) {
            Log.e("Notification Error", "Failed to send notification: ", e)
        }
    }
}
