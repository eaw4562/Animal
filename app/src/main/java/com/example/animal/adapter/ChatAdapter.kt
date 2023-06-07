package com.example.animal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.R
import com.example.animal.dto.ChatDTO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(
    private val context: Context,
    private val messageList: ArrayList<ChatDTO>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val send = 1 // 보내는 타입
    private val receive = 2 // 받는 타입

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == receive) { // 받는 화면
            val view = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            ReceiveViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
            SendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.itemViewType == send) {
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = currentMessage.message
            val date = Date(currentMessage.timestamp!!)
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            viewHolder.timeTextView.text = formattedTime
            if(currentMessage.read){
                viewHolder.isRead.visibility = View.GONE
            }else{
                viewHolder.isRead.visibility = View.VISIBLE
            }
        } else {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
            val date = Date(currentMessage.timestamp!!)
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            viewHolder.timeTextView.text = formattedTime

            if (currentMessage.read) {
                viewHolder.isRead.visibility = View.GONE // 메시지를 읽은 상태인 경우
            } else {
                viewHolder.isRead.visibility = View.VISIBLE // 메시지를 읽지 않은 상태인 경우
            }
        }
    }


    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.uid == currentUserId) {
            send
        } else {
            receive
        }
    }

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.txt_send_message)
        val timeTextView: TextView = itemView.findViewById(R.id.txt_send_date)
        val isRead: TextView = itemView.findViewById(R.id.txt_send_isShown)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
        val timeTextView: TextView = itemView.findViewById(R.id.txt_receive_date)
        val isRead: TextView = itemView.findViewById(R.id.txt_receive_isShown) // isRead가 아니라 isShown으로 변경
    }
}
