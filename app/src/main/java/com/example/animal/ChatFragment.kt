package com.example.animal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animal.adapter.ChatAdapter
import com.example.animal.databinding.FragmentChatBinding
import com.example.animal.dto.ChatDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var currentUserId: String
    private lateinit var currentUserEmail: String

    private lateinit var messageList: ArrayList<ChatDTO>

    private lateinit var contentUid: String
    private lateinit var uid: String
    private lateinit var title: String

    private lateinit var reciverName: String
    private lateinit var reciverUid: String
    private lateinit var senderRoom: String
    private lateinit var reciverRoom: String
    private lateinit var timestamp: String
    lateinit var chatRoom: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid ?: ""
        currentUserEmail = mAuth.currentUser?.email ?: ""
        mDbRef = FirebaseDatabase.getInstance().reference

        arguments?.let {
            contentUid = it.getString("contentUid").toString()
            reciverUid = it.getString("uid").toString()
            title = it.getString("title").toString()

            var senderUid = it.getString("senderUid")
            if (senderUid == null) {
                senderUid = currentUserId
            }

            chatRoom = listOf(senderUid, reciverUid).sortedBy { it }.joinToString(separator = "_")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)

        binding.chatTitleText.text = title

        messageList = ArrayList()
        val chatAdapter: ChatAdapter = ChatAdapter(requireContext(), messageList, currentUserId)
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecycler.adapter = chatAdapter

        binding.chatSendBtn.setOnClickListener {
            val message = binding.chatInputEdit.text.toString()
            val currentTime = System.currentTimeMillis()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val dateString = sdf.format(currentTime)
            val messageObject = ChatDTO(
                currentUserId,
                message,
                reciverUid,
                currentTime,
                dateString
            )

            val senderChatRef =
                mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("messages").push()

            senderChatRef.setValue(messageObject).addOnSuccessListener {
                mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("users")
                    .child(currentUserId).setValue(true)
                mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("users")
                    .child(reciverUid).setValue(true)

                // 상대방이 채팅을 확인한 경우에만 read 값을 변경합니다.
                if (currentUserId == reciverUid) {
                    val messageId = senderChatRef.key
                    messageId?.let {
                        val messageRef = mDbRef.child("Chat").child("chatRooms").child(chatRoom)
                            .child("messages").child(it)
                        messageRef.child("read").setValue(true)
                    }
                }
            }

            binding.chatInputEdit.setText("")
            chatAdapter.notifyDataSetChanged()
            binding.chatRecycler.scrollToPosition(messageList.size - 1)
        }

        val messagesRef = mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("messages")
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(ChatDTO::class.java)
                    messageList.add(message!!)

                    // 상대방이 채팅을 확인한 경우에만 read 값을 변경합니다.
                    if (currentUserId == message.receiverUid && !message.read) {
                        val messageId = postSnapshot.key
                        messageId?.let {
                            val messageRef = messagesRef.child(it)
                            messageRef.child("read").setValue(true)
                        }
                    }
                }

                chatAdapter.notifyDataSetChanged()
                binding.chatRecycler.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        return binding.root
    }
}
