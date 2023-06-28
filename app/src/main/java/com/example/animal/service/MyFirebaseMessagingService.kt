package com.example.animal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.animal.ChatRoomFragment
import com.example.animal.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"

    companion object {
        private const val CHANNEL_ID = "my_channel"

        var firebaseToken: String? = null

        private var instance: MyFirebaseMessagingService? = null

        fun getInstance(): MyFirebaseMessagingService {
            if (instance == null) {
                instance = MyFirebaseMessagingService()
            }
            return instance!!
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token : $token")
        firebaseToken = token

        val pref = getSharedPreferences("token", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token", token).apply()
        editor.commit()
        Log.i(TAG, "Successfully saved the token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            val uid = remoteMessage.data["uid"] ?: return
            val message = remoteMessage.data["message"] ?: return
            val nickname = remoteMessage.data["nickname"] ?: return
            sendNotificationToUser(uid, message, applicationContext)
        } else {
            Log.e(TAG, "Data is empty. Failed to receive the message.")
        }
        super.onMessageReceived(remoteMessage)
    }

    fun sendNotificationToUser(reciverUid: String, message: String, context: Context) {
        val dbRef = FirebaseDatabase.getInstance().reference
        val userRef = dbRef.child("user").child(reciverUid).child("FCMToken")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fcmToken = snapshot.getValue(String::class.java)
                if (fcmToken != null) {
                    val remoteMessage = RemoteMessage.Builder("my_sender_id")
                        .setMessageId("my_message_id")
                        .addData("uid", reciverUid)
                        .addData("message", message)
                        .addData("nickname", "MyNickname")
                        .build()
                    sendNotification(remoteMessage, context)
                } else {
                    Log.e(TAG, "FCM Token not found for the user with UID: $reciverUid")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve FCM Token")
            }
        })
    }

    private fun sendNotification(remoteMessage: RemoteMessage, context: Context) {
        val channelId = CHANNEL_ID
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val uid = remoteMessage.data["uid"]
        val message = remoteMessage.data["message"]
        val nickname = remoteMessage.data["nickname"]

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(nickname)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)

        val intent = Intent(context, ChatRoomFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
            null
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "token=$it")
        }
    }
}
