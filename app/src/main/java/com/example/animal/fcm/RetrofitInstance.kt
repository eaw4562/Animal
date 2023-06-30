package com.example.animal.fcm

import com.example.animal.fcm.Constants.Companion.FCM_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(FCM_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api : FcmInterface by lazy {
        retrofit.create(FcmInterface::class.java)
    }

    // Client
    private fun provideOkHttpClient(
        interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            build()
        }

    // 헤더 추가
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain)
                : Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "key=AAAAneHxwAY:APA91bFi708hG9mwgXbv79TRHNlaVwLJrFPAWtJe-iC3LncyAQW7HuLru7xJPV_SsGXyrJZos0tvHU6hen5MydWJ9Bjoph6q4pl53ShH_34fcil3O9XtCfrbc85Brq2PjBmosa4kn4ez")
                .addHeader("Content-Type", "application/json")
                .build()
            proceed(newRequest)
        }
    }
}