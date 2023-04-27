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

class ChatRoomFragment : Fragment() {

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
        val currentUserId = mAuth.currentUser?.uid

        val chatRoomsRef = mDbRef.child("Chat")
            .orderByChild("chatRoom")
            .startAt("$currentUserId")
            .endAt("$currentUserId" + "\uf8ff")

        chatRoomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val chatRoom = dataSnapshot.key // chatRoom을 가져옴
                    val chatRoomDTO = ChatRoomDTO(chatRoom) // ChatRoomDTO 생성
                    // 가져온 chatRoom을 처리하는 작업 수행
                    chatRooms.add(chatRoomDTO)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 쿼리 수행 실패 시 호출되는 콜백 메서드
            }
        })


        return binding.root
    }

}
