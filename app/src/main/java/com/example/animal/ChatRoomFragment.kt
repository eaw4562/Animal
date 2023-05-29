package com.example.animal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animal.dto.ChatRoomDTO
import com.example.animal.adapter.ChatRoomAdapter
import com.example.animal.databinding.FragmentMyChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatRoomFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var currentUserId: String
    private lateinit var currentUserEmail: String
    private lateinit var binding: FragmentMyChatBinding
    private lateinit var chatRoomKeys: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid ?: ""
        currentUserEmail = mAuth.currentUser?.email ?: ""
        mDbRef = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyChatBinding.inflate(inflater, container, false)
        chatRoomKeys = mutableListOf() // chatRoomKeys 초기화

        //RecyclerView
        val chatRooms: MutableList<ChatRoomDTO> = ArrayList()
        val chatRoomAdapter = ChatRoomAdapter(requireContext(), chatRooms, currentUserEmail)
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecycler.adapter = chatRoomAdapter

        // 채팅방 목록 가져오기
        val currentUserId = mAuth.currentUser?.uid

        mDbRef.child("Chat").child("chatRooms")
            .orderByChild("users/$currentUserId")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {
                        val chatRoom = dataSnapshot.key // 채팅방을 가져옴
                        val chatRoomDTO = ChatRoomDTO(chatRoom) // ChatRoomDTO 생성
                        chatRoomDTO.senderUid = chatRoom?.split("_")?.getOrNull(0)
                        chatRoomDTO.reciverUid = chatRoom?.split("_")?.getOrNull(1)
                        // 마지막 메시지 가져오기
                        val lastMessageRef = mDbRef.child("Chat").child("chatRooms").child(chatRoom!!).child("messages")
                            .limitToLast(1)
                        lastMessageRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(lastMessageSnapshot: DataSnapshot) {
                                if (lastMessageSnapshot.hasChildren()) {
                                    for (childSnapshot in lastMessageSnapshot.children) {
                                        chatRoomDTO.lastMessage = childSnapshot.child("message").value.toString()
                                        chatRoomDTO.dateString = childSnapshot.child("timestamp").value as Long
                                    }
                                }
                                chatRooms.add(chatRoomDTO)
                                chatRoomAdapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // 쿼리 수행 실패 시 호출되는 콜백 메서드
                                Log.e("마지막 메시지 조회 실패", error.message)
                            }
                        })
                        //채팅을 보낸 사용자 이름 가져오기
                        val userRef = mDbRef.child("user")
                        for(uidSnapshot in dataSnapshot.child("users").children){
                            val uid = uidSnapshot.key.toString()
                            if(uid != currentUserId){
                                userRef.child(uid).child("nickname")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(nameSnapshot: DataSnapshot) {
                                            val otherUserName = nameSnapshot.value.toString()
                                            chatRoomDTO.receiverEmail = otherUserName
                                            chatRoomAdapter.notifyDataSetChanged()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                            }
                        }
                    }
                    chatRoomAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // 쿼리 수행 실패 시 호출되는 콜백 메서드
                    Log.e("채팅방 조회 실패", error.message)
                }
            })


        return binding.root
    }
}