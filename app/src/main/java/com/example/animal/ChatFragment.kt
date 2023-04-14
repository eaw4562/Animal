package com.example.animal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animal.DTO.ChatDTO
import com.example.animal.adapter.ChatAdapter
import com.example.animal.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var currentUserId: String

    private lateinit var chatList: ArrayList<ChatDTO>
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var contentUid: String
    private lateinit var uid: String
    private lateinit var title: String

    private lateinit var reciverName: String
    private lateinit var reciverUid: String
    private lateinit var reciverRoom : String //받는 대화방
    private lateinit var senderRoom: String //보낸 대화방


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid ?: ""
        mDbRef = FirebaseDatabase.getInstance().reference

        // 전달 받은 bundle에서 값을 받아옴
        arguments?.let {
            contentUid = it.getString("contentUid").toString()
            reciverUid = it.getString("uid").toString()
            title = it.getString("title").toString()

            //접속자 Uid
            val senderUid = mAuth.currentUser?.uid

            //보낸이 방
            senderRoom = reciverUid + senderUid

            //받는이 방
            reciverRoom = senderUid + reciverUid
        }




        // 전달 받은 bundle에서 값을 받아옴
        arguments?.let {
            contentUid = it.getString("contentUid").toString()
            reciverUid = it.getString("uid").toString()
            title = it.getString("title").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)

        // 게시글의 타이틀을 출력
        binding.chatTitleText.text = title


        //RecyclerView
        chatList = ArrayList()
        chatAdapter = ChatAdapter(requireContext(), ArrayList())
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecycler.adapter = chatAdapter


        // 메시지 전송 버튼에 대한 onClick 이벤트 설정
        binding.chatSendBtn.setOnClickListener {
            val message = binding.chatInputEdit.text.toString()
            val messageObject = ChatDTO(currentUserId, message)

            //데이터 저장
            mDbRef.child("chat").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //채팅 저장 성공
                    mDbRef.child("chats").child(reciverRoom).child("messages").push()
                        .setValue(messageObject)
                }
            //입력값 초기화
            binding.chatInputEdit.setText("")
        }

        //메시지 가져오기
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()

                    for(postSnapshat in snapshot.children){

                        val chat = postSnapshat.getValue(ChatDTO::class.java)
                        chatList.add(chat!!)
                    }
                    //적용
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        return binding.root
    }
}
