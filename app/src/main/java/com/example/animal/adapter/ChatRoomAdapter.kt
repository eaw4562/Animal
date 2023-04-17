package com.example.animal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.DTO.ChatRoomDTO
import com.example.animal.R
import java.text.DateFormat
import java.util.*

class ChatRoomAdapter(
    private val context: Context,
    private val chatRooms: List<ChatRoomDTO>,
    private val currentUserEmail: String
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_room_list, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        val otherUserEmail = if (chatRoom.senderEmail == currentUserEmail) {
            chatRoom.receiverEmail
        } else {
            chatRoom.senderEmail
        }
        holder.chatRoomNameTextView.text = otherUserEmail
        holder.lastMessageTextView.text = chatRoom.lastMessage
        holder.timestampTextView.text = DateFormat.getTimeInstance().format(Date(chatRoom.timestamp))
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatRoomNameTextView: TextView = itemView.findViewById(R.id.chat_room_id)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.chat_room_last_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestamp_text)
    }

}
