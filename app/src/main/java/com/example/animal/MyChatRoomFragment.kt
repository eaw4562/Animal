package com.example.animal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animal.DTO.ChatRoomDTO
import com.example.animal.adapter.ChatRoomAdapter
import com.example.animal.databinding.FragmentMyChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyChatRoomFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var currentUserId: String
    private lateinit var currentUserEmail: String
    private lateinit var binding: FragmentMyChatBinding

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

        //RecyclerView
        val chatRooms: MutableList<ChatRoomDTO> = ArrayList()
        val chatRoomAdapter = ChatRoomAdapter(requireContext(), chatRooms, currentUserEmail)
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecycler.adapter = chatRoomAdapter

        // 채팅방 목록 가져오기
        mDbRef.child("chats")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatRooms.clear()

                    for (postSnapshat in snapshot.children) {
                        val chatRoom = postSnapshat.getValue(ChatRoomDTO::class.java)
                        if (chatRoom?.senderUid == currentUserId || chatRoom?.receiverUid == currentUserId) {
                            chatRooms.add(chatRoom)
                        }
                    }
                    Log.w("MyChatRoomFragment", "chatRooms size: ${chatRooms.size}") // 새로 추가한 로그

                    // 채팅방 목록 업데이트
                    chatRoomAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        return binding.root
    }

}
