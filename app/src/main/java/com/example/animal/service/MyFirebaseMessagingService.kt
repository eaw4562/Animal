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
import com.example.animal.LoginActivity
import com.example.animal.MainActivity
import com.example.animal.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    /** Token 생성 메서드(FirebaseInstanceIdService 사라짐) */
    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token : $token")

        //토큰 값을 따로 저장
        val pref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token", token).apply()
        editor.commit()
        Log.i(TAG, "성공적으로 토큰을 저장함")
    }

    /**메시지 수신 메서드(포그라운드) */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From : " + remoteMessage!!.from)

        //Notification 메시지를 수신할 경우
        //reomteMessage.notifivation?.bofy!! 여기에 내용이 저장되잇음
        //Log.d(TAG, "Notification Message Body : " + remotMessage.notification?.body!!)

        //받은 remoteMessage의 값 출력해보기. 데이터메시지 / 알림메시지
        Log.d(TAG,"Meesage data : ${remoteMessage.data}")
        Log.d(TAG, "Meesage noti : ${remoteMessage.notification}")

        if(remoteMessage.data.isNotEmpty()){
            //알림 생성
            sendNotification(remoteMessage)
            //Log.d(TAG, remoteMessage.data["title"].toString())
            //Log.d(TAG, remoteMessage.data["body"].toString())
        }else{
            Log.e(TAG, "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
        }
        super.onMessageReceived(remoteMessage)
    }
    private fun sendNotification(remoteMessage: RemoteMessage){
        //RequestCode Id를 고유 값으로 지정하여 알림이 개별 표시
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()
        //일회용 PendingIntent : Intent의 실행 권한을 외부의 어플리케이션에게 위임
        val intent = Intent(this, LoginActivity::class.java)
        //각 ket, value 추가
        for(key in remoteMessage.data.keys){
            intent.putExtra(key,remoteMessage.data.getValue(key))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)//Activity stack 을 경로만 남김 (A-B-C-D-B => A-B)

        //23.05.22 Android 최신버전 대응 (FLAG_MUTABLE, FLAG_IMMUTABLE)
        //PendingIntent.FLAG_MUTABLE은 PendingIntent의 내용을 변경할 수 있도록 허용, PendingIntent.FLAG_IMMUTABLE은 PendingIntent의 내용을 변경할 수 없음
        //val pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT)
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent,
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)

        //알림 채널 이름
        val channelId = "my_channel"
        //알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //알림에 대한 UI 정보, 작업
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) //아이콘 설정
            .setContentTitle(remoteMessage.data["title"].toString()) //제목
            .setContentText(remoteMessage.data["body"].toString()) //메시지 내용
            .setAutoCancel(true) //알림클릭시 삭제여부
            .setSound(soundUri)
            .setContentIntent(pendingIntent) //알림 실행 시 Intent

        val notificationManaget = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //오레오 버전 이후에는 채널이 필요
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManaget.createNotificationChannel(channel)
        }
        //알림 생성
        notificationManaget.notify(uniId, notificationBuilder.build())
    }
    /**Token 가져오기 */
    fun getFirebaseToken() {
        //비동기 방식
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG,"token=${it}")
        }
    }
}