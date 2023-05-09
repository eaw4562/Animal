package com.example.animal.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.ChatFragment
import com.example.animal.DTO.ChatRoomDTO
import com.example.animal.MainActivity
import com.example.animal.R
import java.text.DateFormat
import java.util.*

class ChatRoomAdapter(
    private val context: Context,
    private val chatRooms: List<ChatRoomDTO>,
    private val currentUserUid: String
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_room_list, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        val otherUserUid = if (chatRoom.senderUid == currentUserUid) {
            chatRoom.senderUid
        } else {
            chatRoom.reciverUid
        }
        val otherUserEmail = chatRoom.receiverEmail // 수정된 부분
        holder.chatRoomNameTextView.text = otherUserEmail
        holder.lastMessageTextView.text = chatRoom.lastMessage
        val dateFormat = DateFormat.getTimeInstance()
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        holder.timestampTextView.text = dateFormat.format(Date(chatRoom.dateString))

        holder.itemView.setOnClickListener {
            // ChatFragment로 이동하면서 클릭한 ChatRoomDTO 객체를 전달
            val bundle = Bundle()
            bundle.putString("uid", otherUserUid)
            bundle.putString("title", otherUserEmail)
            val chatFragment = ChatFragment()
            chatFragment.arguments = bundle
            val transaction = (context as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView, chatFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatRoomNameTextView: TextView = itemView.findViewById(R.id.chat_room_id)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.chat_room_last_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.chat_room_timestamp)
    }

}