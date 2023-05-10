package com.example.animal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.DTO.ChatDTO
import com.example.animal.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private val context: Context, private val messageList: ArrayList<ChatDTO>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val send = 1  //보내는 타입
    private val receive = 2 //받는 타입

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 1) { //받는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            ReceiveViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
            SendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = currentMessage.message
            val date = Date(currentMessage.timestamp!!)
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val formattedTime = dateFormat.format(date)
            viewHolder.timeTextView.text = formattedTime

        } else {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
            val date = Date(currentMessage.timestamp!!)
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            viewHolder.timeTextView.text = formattedTime
        }
    }



    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)){
            send
        }else{
            receive
        }
    }

    class SendViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val sendMessage: TextView = itemView.findViewById(R.id.txt_message)
        val timeTextView : TextView = itemView.findViewById(R.id.txt_date)
    }

    class ReceiveViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_message)
        val timeTextView : TextView = itemView.findViewById(R.id.txt_date)
    }
}